<script lang="ts">
	import { onMount } from 'svelte';
	import { writable } from 'svelte/store';
	import type { Product, Category, ApiResponse } from '$lib/types';

	// Stores để quản lý state
	const books = writable<Product[]>([]);
	const categories = writable<Category[]>([]);
	const loading = writable<boolean>(false);
	const error = writable<string>('');

	// API URLs
	const API_BASE = 'http://localhost:8080/api/user';

	// Fetch books from API
	async function fetchBooks(): Promise<void> {
		loading.set(true);
		try {
			const response = await fetch(`${API_BASE}/books`);
			const result: ApiResponse<Product[]> = await response.json();
			
			if (result.success && result.data) {
				books.set(result.data);
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

	// Format price to Vietnamese currency
	function formatPrice(price: number): string {
		return new Intl.NumberFormat('vi-VN', {
			style: 'currency',
			currency: 'VND'
		}).format(price);
	}

	// Get primary image URL or return null for default
	function getPrimaryImageUrl(images?: any[]): string | null {
		if (!images || images.length === 0) return null;
		const primaryImage = images.find(img => img.isPrimary);
		return primaryImage ? primaryImage.imageUrl : images[0].imageUrl;
	}
	onMount(async () => {
		loading.set(true);
		await Promise.all([fetchBooks(), fetchCategories()]);
		loading.set(false);
	});
</script>

<svelte:head>
	<title>Trang chủ - BookStore</title>
</svelte:head>

<!-- Page content -->
<div class="max-w-7xl mx-auto py-12 px-4 sm:px-6 lg:px-8">

	{#if $loading}
		<div class="flex justify-center items-center min-h-[400px]">
			<div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
		</div>
	{:else if $error}
		<div class="bg-red-50 border border-red-200 rounded-md p-4 mb-8">
			<div class="flex">
				<div class="flex-shrink-0">
					<svg class="h-5 w-5 text-red-400" viewBox="0 0 20 20" fill="currentColor">
						<path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd" />
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
	{:else}
		<!-- Categories section -->
		{#if $categories.length > 0}
			<div class="mb-12">
				<h2 class="text-2xl font-bold text-gray-900 mb-6">Danh mục sách</h2>
				<div class="flex flex-wrap gap-4">
					{#each $categories as category}
						<div class="bg-white border border-gray-200 rounded-lg px-4 py-2 hover:bg-blue-50 hover:border-blue-300 transition-colors duration-200 cursor-pointer">
							<span class="text-gray-700 font-medium">{category.categoryName}</span>
						</div>
					{/each}
				</div>
			</div>
		{/if}

		<!-- Featured books section -->
		<div class="mt-16">
			<h2 class="text-2xl font-bold text-gray-900 mb-8">
				{$books.length > 0 ? 'Danh sách sách' : 'Không có sách nào'}
			</h2>
			
			{#if $books.length > 0}
				<div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
					{#each $books as book}
						<div class="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition-shadow duration-200">
							<div class="h-48 bg-gray-200 flex items-center justify-center">
								{#if getPrimaryImageUrl(book.images)}
									<img 
										src={getPrimaryImageUrl(book.images)} 
										alt={book.title}
										class="w-full h-full object-cover"
									/>
								{:else}
									<svg class="w-16 h-16 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
										<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.746 0 3.332.477 4.5 1.253v13C19.832 18.477 18.246 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"/>
									</svg>
								{/if}
							</div>
							<div class="p-4">
								<h3 class="font-semibold text-gray-900 mb-2 line-clamp-2" title={book.title}>
									{book.title}
								</h3>
								<p class="text-gray-600 text-sm mb-2">{book.author || 'Tác giả không xác định'}</p>
								{#if book.categories && book.categories.length > 0}
									<div class="mb-2">
										<div class="flex flex-wrap gap-1">
											{#each book.categories.slice(0, 2) as category}
												<span class="inline-block bg-blue-100 text-blue-800 text-xs px-2 py-1 rounded">
													{category.categoryName}
												</span>
											{/each}
											{#if book.categories.length > 2}
												<span class="inline-block bg-gray-100 text-gray-600 text-xs px-2 py-1 rounded">
													+{book.categories.length - 2}
												</span>
											{/if}
										</div>
									</div>
								{/if}
								<div class="flex justify-between items-center">
									<span class="text-lg font-bold text-blue-600">{formatPrice(book.price)}</span>
									{#if book.stockQuantity !== undefined && book.stockQuantity > 0}
										<button 
											class="px-3 py-1 bg-blue-600 text-white text-sm rounded hover:bg-blue-700 transition-colors duration-200"
											disabled
											title="Chức năng thêm vào giỏ hàng chưa được triển khai"
										>
											Thêm vào giỏ
										</button>
									{:else}
										<span class="px-3 py-1 bg-gray-300 text-gray-600 text-sm rounded">
											Hết hàng
										</span>
									{/if}
								</div>
							</div>
						</div>
					{/each}
				</div>
			{:else}
				<div class="text-center py-12">
					<svg class="mx-auto h-24 w-24 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
						<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.746 0 3.332.477 4.5 1.253v13C19.832 18.477 18.246 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"/>
					</svg>
					<h3 class="mt-4 text-lg font-medium text-gray-900">Chưa có sách nào</h3>
					<p class="mt-2 text-gray-600">Hiện tại chưa có sách nào trong hệ thống.</p>
				</div>
			{/if}
		</div>
	{/if}
</div>