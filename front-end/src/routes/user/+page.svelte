<script lang="ts">
	import { onMount } from 'svelte';
	import { writable, derived } from 'svelte/store';
	import type { Product, Category, ApiResponse } from '$lib/types';
	import { getPrimaryImageUrl } from '$lib/imageUtils';

	// Stores để quản lý state
	const books = writable<Product[]>([]);
	const categories = writable<Category[]>([]);
	const loading = writable<boolean>(false);
	const error = writable<string>('');
	const currentUser = writable<any>(null);
	const addingToCart = writable<Set<number>>(new Set());

	// Filter states
	const searchTerm = writable<string>('');
	const selectedCategory = writable<string>('');
	const sortBy = writable<string>('');
	const stockFilter = writable<string>('');

	// API URLs
	const API_BASE = 'http://localhost:8080/api/user';
	const AUTH_API = 'http://localhost:8080/api/auth';
	// Fetch books from API
	async function fetchBooks(): Promise<void> {
		loading.set(true);
		try {
			const response = await fetch(`${API_BASE}/books`);
			const result: ApiResponse<Product[]> = await response.json();

			if (result.success && result.data) {
				books.set(result.data);
				// Debug: Log the first book's images to see the structure
				if (result.data.length > 0 && result.data[0].images) {
					console.log('First book images:', result.data[0].images);
					console.log('Generated image URL:', getPrimaryImageUrl(result.data[0].images));
				}
				error.set('');
			} else {
				error.set(result.message || 'Lỗi khi lấy danh sách sách');
			}
		} catch (err) {
			error.set('Không thể tải danh sách sách');
			console.error('Error fetching books:', err);
		} finally {
			loading.set(false);
		}
	}
	// Fetch categories from API
	async function fetchCategories(): Promise<void> {
		try {
			const response = await fetch(`${API_BASE}/categories`);
			const result: ApiResponse<Category[]> = await response.json();

			if (result.success && result.data) {
				categories.set(result.data);
			} else {
				console.error('Error fetching categories:', result.message);
			}
		} catch (err) {
			console.error('Error fetching categories:', err);
		}
	}
	// Get current user info
	async function getCurrentUser(): Promise<void> {
		try {
			const token = localStorage.getItem('token');
			if (!token) return;

			const response = await fetch(`${AUTH_API}/me`, {
				headers: {
					'Authorization': `Bearer ${token}`,
					'Content-Type': 'application/json'
				}
			});

			if (response.ok) {
				const userData = await response.json();
				// API /me trả về trực tiếp UserDTO, không có wrapper success/data
				currentUser.set(userData);
			}
		} catch (err) {
			console.error('Error fetching current user:', err);
		}
	}

	// Add book to shopping cart
	async function addToCart(bookId: number): Promise<void> {
		const user = $currentUser;
		if (!user) {
			alert('Vui lòng đăng nhập để thêm sách vào giỏ hàng');
			return;
		}

		// Add bookId to loading set
		addingToCart.update(set => new Set(set).add(bookId));

		try {
			const token = localStorage.getItem('token');
			const response = await fetch(`${API_BASE}/shoppingcart/${bookId}`, {
				method: 'POST',
				headers: {
					'Authorization': `Bearer ${token}`,
					'Content-Type': 'application/json'
				},
				body: JSON.stringify({ userId: user.userId })
			});

			const result = await response.json();

			if (result.success) {
				alert('Đã thêm sách vào giỏ hàng thành công!');
			} else {
				alert(result.message || 'Có lỗi xảy ra khi thêm sách vào giỏ hàng');
			}
		} catch (err) {
			console.error('Error adding to cart:', err);
			alert('Không thể thêm sách vào giỏ hàng');
		} finally {
			// Remove bookId from loading set
			addingToCart.update(set => {
				const newSet = new Set(set);
				newSet.delete(bookId);
				return newSet;
			});
		}
	}
	// Format price to Vietnamese currency
	function formatPrice(price: number): string {
		return new Intl.NumberFormat('vi-VN', {
			style: 'currency',
			currency: 'VND'
		}).format(price);
	}

	// Filtered and sorted books using derived store
	const filteredBooks = derived(
		[books, searchTerm, selectedCategory, sortBy, stockFilter],
		([$books, $searchTerm, $selectedCategory, $sortBy, $stockFilter]) => {
			let filtered = [...$books];

			// Filter by search term (title or author)
			if ($searchTerm.trim()) {
				const searchLower = $searchTerm.toLowerCase().trim();
				filtered = filtered.filter(
					(book) =>
						book.title.toLowerCase().includes(searchLower) ||
						(book.author && book.author.toLowerCase().includes(searchLower))
				);
			}

			// Filter by category
			if ($selectedCategory && $selectedCategory !== '') {
				filtered = filtered.filter(
					(book) =>
						book.categories &&
						book.categories.some((cat) => cat.categoryId.toString() === $selectedCategory)
				);
			} // Filter by stock status
			if ($stockFilter) {
				if ($stockFilter === 'in-stock') {
					filtered = filtered.filter((book) => book.stockQuantity && book.stockQuantity > 0);
				} else if ($stockFilter === 'out-of-stock') {
					filtered = filtered.filter((book) => !book.stockQuantity || book.stockQuantity === 0);
				}
			}

			// Sort books
			if ($sortBy) {
				filtered.sort((a, b) => {
					switch ($sortBy) {
						case 'price-asc':
							return a.price - b.price;
						case 'price-desc':
							return b.price - a.price;
						case 'title-asc':
							return a.title.localeCompare(b.title, 'vi');
						case 'title-desc':
							return b.title.localeCompare(a.title, 'vi');
						default:
							return 0;
					}
				});
			}

			return filtered;
		}
	);

	// Clear all filters
	function clearFilters() {
		searchTerm.set('');
		selectedCategory.set('');
		sortBy.set('');
		stockFilter.set('');
	}	onMount(async () => {
		loading.set(true);

		try {
			// Gọi API thật từ UserHomeController và lấy thông tin user
			await Promise.all([fetchBooks(), fetchCategories(), getCurrentUser()]);
		} catch (err) {
			console.error('Error loading data:', err);
			error.set('Không thể tải dữ liệu từ server');
		}

		loading.set(false);
	});
</script>

<svelte:head>
	<title>Trang chủ - BookStore</title>
</svelte:head>

<!-- Page content -->
<div class="mx-auto max-w-7xl px-4 py-12 sm:px-6 lg:px-8">
	{#if $loading}
		<div class="flex min-h-[400px] items-center justify-center">
			<div class="h-12 w-12 animate-spin rounded-full border-b-2 border-blue-600"></div>
		</div>
	{:else if $error}
		<div class="mb-8 rounded-md border border-red-200 bg-red-50 p-4">
			<div class="flex">
				<div class="flex-shrink-0">
					<svg class="h-5 w-5 text-red-400" viewBox="0 0 20 20" fill="currentColor">
						<path
							fill-rule="evenodd"
							d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z"
							clip-rule="evenodd"
						/>
					</svg>
				</div>
				<div class="ml-3">
					<h3 class="text-sm font-medium text-red-800">Lỗi</h3>
					<div class="mt-2 text-sm text-red-700">
						<p>{$error}</p>
					</div>
				</div>
			</div>
		</div>
	{:else}		<!-- Search and Filter Section -->		<div class="mb-8">
			<div class="rounded-xl border border-gray-200 bg-gradient-to-br from-white via-blue-50/30 to-indigo-50/50 p-6 shadow-lg backdrop-blur-sm">
				<div class="mb-6 flex items-center gap-3">
					<div class="flex h-10 w-10 items-center justify-center rounded-xl bg-gradient-to-br from-blue-500 to-indigo-600 shadow-md">
						<svg class="h-5 w-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
							<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/>
						</svg>
					</div>
					<h2 class="text-xl font-bold bg-gradient-to-r from-gray-900 to-gray-700 bg-clip-text text-transparent">Tìm kiếm và lọc sách</h2>
				</div>				<div class="grid grid-cols-1 gap-4 lg:grid-cols-6 xl:grid-cols-12">
					<!-- Search Input -->
					<div class="lg:col-span-3 xl:col-span-5">
						<div class="relative group">
							<div class="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3 transition-colors group-focus-within:text-blue-500">
								<svg class="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
									<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/>
								</svg>
							</div>
							<input
								id="search"
								type="text"
								bind:value={$searchTerm}
								placeholder="Tìm kiếm theo tên sách hoặc tác giả..."
								class="block w-full rounded-xl border border-gray-300 bg-white/80 backdrop-blur-sm py-3 pl-10 pr-4 text-sm shadow-sm transition-all duration-300 placeholder:text-gray-400 hover:border-gray-400 focus:border-blue-500 focus:bg-white focus:outline-none focus:ring-4 focus:ring-blue-500/10 focus:shadow-md"
							/>
							{#if $searchTerm}								<button
									type="button"
									on:click={() => searchTerm.set('')}
									class="absolute inset-y-0 right-0 flex items-center pr-3 text-gray-400 hover:text-gray-600 transition-colors"
									title="Xóa tìm kiếm"
									aria-label="Xóa tìm kiếm"
								>
									<svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
										<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
									</svg>
								</button>
							{/if}
						</div>
					</div>					<!-- Category Filter -->
					<div class="lg:col-span-1 xl:col-span-2">
						<div class="relative group">
							<select
								id="category"
								bind:value={$selectedCategory}
								class="block w-full appearance-none rounded-xl border border-gray-300 bg-white/80 backdrop-blur-sm py-3 px-4 pr-10 text-sm shadow-sm transition-all duration-300 hover:border-gray-400 focus:border-blue-500 focus:bg-white focus:outline-none focus:ring-4 focus:ring-blue-500/10 focus:shadow-md cursor-pointer"
							>
								<option value="">Tất cả danh mục</option>
								{#each $categories as category}
									<option value={category.categoryId.toString()}>{category.categoryName}</option>
								{/each}
							</select>
							<div class="pointer-events-none absolute inset-y-0 right-0 flex items-center px-3 text-gray-400 group-focus-within:text-blue-500 transition-colors">
								<svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
									<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"/>
								</svg>
							</div>
						</div>
					</div>

					<!-- Sort Filter -->
					<div class="lg:col-span-1 xl:col-span-2">
						<div class="relative group">
							<select
								id="sort"
								bind:value={$sortBy}
								class="block w-full appearance-none rounded-xl border border-gray-300 bg-white/80 backdrop-blur-sm py-3 px-4 pr-10 text-sm shadow-sm transition-all duration-300 hover:border-gray-400 focus:border-blue-500 focus:bg-white focus:outline-none focus:ring-4 focus:ring-blue-500/10 focus:shadow-md cursor-pointer"
							>
								<option value="">Sắp xếp</option>
								<option value="price-asc">Giá: Thấp → Cao</option>
								<option value="price-desc">Giá: Cao → Thấp</option>
								<option value="title-asc">Tên: A → Z</option>
								<option value="title-desc">Tên: Z → A</option>
							</select>
							<div class="pointer-events-none absolute inset-y-0 right-0 flex items-center px-3 text-gray-400 group-focus-within:text-blue-500 transition-colors">
								<svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
									<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 7v10a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-6l-2-2H5a2 2 0 00-2 2z"/>
								</svg>
							</div>
						</div>
					</div>

					<!-- Stock Filter -->
					<div class="lg:col-span-1 xl:col-span-2">
						<div class="relative group">
							<select
								id="stock"
								bind:value={$stockFilter}
								class="block w-full appearance-none rounded-xl border border-gray-300 bg-white/80 backdrop-blur-sm py-3 px-4 pr-10 text-sm shadow-sm transition-all duration-300 hover:border-gray-400 focus:border-blue-500 focus:bg-white focus:outline-none focus:ring-4 focus:ring-blue-500/10 focus:shadow-md cursor-pointer"
							>
								<option value="">Tình trạng</option>
								<option value="in-stock">Còn hàng</option>
								<option value="out-of-stock">Hết hàng</option>
							</select>
							<div class="pointer-events-none absolute inset-y-0 right-0 flex items-center px-3 text-gray-400 group-focus-within:text-blue-500 transition-colors">
								<svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
									<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"/>
								</svg>
							</div>						</div>
					</div>

					<!-- Clear Filters Button -->
					<div class="flex lg:col-span-1 xl:col-span-1">
						<button
							type="button"
							on:click={clearFilters}
							class="flex w-full items-center justify-center gap-2 rounded-xl border border-gray-300 bg-gradient-to-r from-white to-gray-50 backdrop-blur-sm px-4 py-3 text-sm font-medium text-gray-700 shadow-sm transition-all duration-300 hover:from-gray-50 hover:to-gray-100 hover:border-gray-400 hover:shadow-md focus:outline-none focus:ring-4 focus:ring-gray-500/10 active:scale-95"
							title="Xóa tất cả bộ lọc"
						>
							<svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
								<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
							</svg>
							<span class="hidden sm:inline">Xóa</span>
						</button>
					</div>
				</div>

				<!-- Filter Results Summary -->
				<div class="mt-6 flex items-center justify-between border-t border-gray-200/60 pt-4">
					<div class="flex items-center gap-3">
						<!-- <p class="text-sm text-gray-600">
							Hiển thị <span class="font-semibold text-blue-600">{$filteredBooks.length}</span>
							trong tổng số <span class="font-semibold text-gray-900">{$books.length}</span> sách
						</p> -->
						{#if $filteredBooks.length !== $books.length}
							<div class="hidden sm:flex items-center gap-2 text-xs text-amber-600 bg-amber-50 px-3 py-1 rounded-full border border-amber-200">
								<svg class="h-3 w-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
									<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L4.082 15.5c-.77.833.192 2.5 1.732 2.5z"/>
								</svg>
								Kết quả đã được lọc
							</div>
						{/if}
					</div>
					{#if $searchTerm || $selectedCategory || $sortBy || $stockFilter}
						<div class="flex items-center gap-2 text-xs text-blue-600 bg-blue-50 px-3 py-1 rounded-full border border-blue-200">
							<span class="flex h-2 w-2 rounded-full bg-blue-500 animate-pulse"></span>
							Đã áp dụng bộ lọc
						</div>
					{/if}
				</div>
			</div>
		</div>

		<!-- Categories section
		{#if $categories.length > 0}
			<div class="mb-12">
				<h2 class="text-2xl font-bold text-gray-900 mb-6">Danh mục sách</h2>
				<div class="flex flex-wrap gap-4">
					{#each $categories as category}
						<button
							class="bg-white border border-gray-200 rounded-lg px-4 py-2 hover:bg-blue-50 hover:border-blue-300 transition-colors duration-200 focus:outline-none focus:ring-2 focus:ring-blue-500 {$selectedCategory === category.categoryId.toString() ? 'bg-blue-50 border-blue-300' : ''}"
							on:click={() => {
								if ($selectedCategory === category.categoryId.toString()) {
									selectedCategory.set('');
								} else {
									selectedCategory.set(category.categoryId.toString());
								}
							}}
						>
							<span class="text-gray-700 font-medium">{category.categoryName}</span>
						</button>
					{/each}
				</div>
			</div>
		{/if} -->
		<!-- Featured books section -->
		<div class="mt-16">
			<h2 class="mb-8 text-2xl font-bold text-gray-900">
				{$filteredBooks.length > 0 ? 'Danh sách sách' : 'Không có sách nào'}
			</h2>

			{#if $filteredBooks.length > 0}
				<div class="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-4">
					{#each $filteredBooks as book}
						<div
							class="overflow-hidden rounded-lg bg-white shadow-md transition-shadow duration-200 hover:shadow-lg"
						>							<div class="book-image-container flex h-48 items-center justify-center">
								{#if book.images && book.images.length > 0}
									<img
										src={getPrimaryImageUrl(book.images)}
										alt={book.title}
										class="book-image max-h-full max-w-full object-contain"
										on:load={() => {
											console.log('Image loaded successfully for:', book.title);
										}}
										on:error={(e) => {
										console.warn('Failed to load image for book:', book.title);
										console.warn('Image URL was:', getPrimaryImageUrl(book.images ?? []));
										const img = e.currentTarget as HTMLImageElement;
										const placeholder = img.nextElementSibling as HTMLElement;
										img.style.display = 'none';
										if (placeholder) {
											placeholder.style.display = 'flex';
										}
									}}
									/>									<!-- Fallback placeholder (hidden by default) -->
									<div class="hidden h-full w-full items-center justify-center rounded bg-gray-100">
										<div class="text-center">
											<svg
												class="mx-auto h-16 w-16 text-gray-400 mb-2"
												fill="none"
												stroke="currentColor"
												viewBox="0 0 24 24"
											>
												<path
													stroke-linecap="round"
													stroke-linejoin="round"
													stroke-width="2"
													d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"
												/>
											</svg>
											<p class="text-xs text-gray-500">Không có ảnh</p>
										</div>
									</div>
								{:else}
									<!-- No images available -->
									<div class="h-full w-full flex items-center justify-center rounded bg-gray-100">
										<div class="text-center">
											<svg
												class="mx-auto h-16 w-16 text-gray-400 mb-2"
												fill="none"
												stroke="currentColor"
												viewBox="0 0 24 24"
											>
												<path
													stroke-linecap="round"
													stroke-linejoin="round"
													stroke-width="2"
													d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"
												/>
											</svg>
											<p class="text-xs text-gray-500">Không có ảnh</p>
										</div>
									</div>
								{/if}
							</div>
							<div class="p-4">
								<h3 class="mb-2 line-clamp-2 font-semibold text-gray-900" title={book.title}>
									{book.title}
								</h3>
								<p class="mb-2 text-sm text-gray-600">{book.author || 'Tác giả không xác định'}</p>
								{#if book.categories && book.categories.length > 0}
									<div class="mb-2">
										<div class="flex flex-wrap gap-1">
											{#each book.categories.slice(0, 2) as category}
												<span
													class="inline-block rounded bg-blue-100 px-2 py-1 text-xs text-blue-800"
												>
													{category.categoryName}
												</span>
											{/each}
											{#if book.categories.length > 2}
												<span
													class="inline-block rounded bg-gray-100 px-2 py-1 text-xs text-gray-600"
												>
													+{book.categories.length - 2}
												</span>
											{/if}
										</div>
									</div>
								{/if}								<div class="flex items-center justify-between">
									<span class="text-lg font-bold text-blue-600">{formatPrice(book.price)}</span>
									{#if book.stockQuantity !== undefined && book.stockQuantity > 0}
										<button
											class="rounded bg-blue-600 px-3 py-1 text-sm text-white transition-colors duration-200 hover:bg-blue-700 disabled:bg-gray-400 disabled:cursor-not-allowed flex items-center gap-1"
											disabled={$addingToCart.has(book.bookId)}
											on:click={() => addToCart(book.bookId)}
										>
											{#if $addingToCart.has(book.bookId)}
												<svg class="w-3 h-3 animate-spin" fill="none" stroke="currentColor" viewBox="0 0 24 24">
													<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"/>
												</svg>
												Đang thêm...
											{:else}
												Thêm vào giỏ
											{/if}
										</button>
									{:else}
										<span class="rounded bg-gray-300 px-3 py-1 text-sm text-gray-600">
											Hết hàng
										</span>
									{/if}
								</div>
							</div>
						</div>
					{/each}
				</div>
			{:else}
				<div class="py-12 text-center">
					<svg
						class="mx-auto h-24 w-24 text-gray-400"
						fill="none"
						stroke="currentColor"
						viewBox="0 0 24 24"
					>
						<path
							stroke-linecap="round"
							stroke-linejoin="round"
							stroke-width="2"
							d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.746 0 3.332.477 4.5 1.253v13C19.832 18.477 18.246 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"
						/>
					</svg>
					<h3 class="mt-4 text-lg font-medium text-gray-900">Chưa có sách nào</h3>
					<p class="mt-2 text-gray-600">Hiện tại chưa có sách nào trong hệ thống.</p>
				</div>
			{/if}
		</div>
	{/if}
</div>

<style>
	.line-clamp-2 {
		display: -webkit-box;
		-webkit-line-clamp: 2;
		line-clamp: 2;
		-webkit-box-orient: vertical;
		overflow: hidden;
		text-overflow: ellipsis;
	}

	/* Improve image display */
	.book-image-container {
		background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
		border-radius: 0.5rem;
		padding: 0.5rem;
	}

	.book-image {
		transition: transform 0.2s ease-in-out;
		border-radius: 0.25rem;
		box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
	}

	.book-image:hover {
		transform: scale(1.02);
	}

	/* Loading shimmer effect */
	.image-loading {
		background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
		background-size: 200% 100%;
		animation: shimmer 1.5s infinite;
	}

	@keyframes shimmer {
		0% {
			background-position: -200% 0;
		}
		100% {
			background-position: 200% 0;
		}
	}
</style>
