<!-- front-end/src/routes/admin/products/+page.svelte -->
<script lang="ts">
	import { onMount } from 'svelte';
	import { writable } from 'svelte/store';
	import type { Product, ProductFormData, Category, ApiResponse } from '$lib/types';

	// Stores
	const products = writable<Product[]>([]);
	const categories = writable<Category[]>([]);
	const loading = writable<boolean>(false);
	const message = writable<string>('');
	const error = writable<string>('');
	const showModal = writable<boolean>(false);
	const editMode = writable<boolean>(false);

	// Form data
	let formData: ProductFormData = {
		title: '',
		author: '',
		publisher: '',
		publicationYear: null,
		description: '',
		price: null,
		categoryIds: [],
		primaryImageUrl: ''
	};

	let editProductId: number | null = null;

	// API Base URLs
	const API_BASE = 'http://localhost:8080/api/admin/products';
	const CATEGORIES_API = `${API_BASE}/categories`;

	// Functions
	async function fetchProducts(): Promise<void> {
		loading.set(true);
		try {
			const response = await fetch(API_BASE);
			const result: ApiResponse<Product[]> = await response.json();
			if (result.success && result.data) {
				products.set(result.data);
			} else {
				error.set(result.message);
			}
		} catch (err) {
			error.set('Lỗi kết nối: ' + (err as Error).message);
		} finally {
			loading.set(false);
		}
	}

	async function fetchCategories(): Promise<void> {
		try {
			const response = await fetch(CATEGORIES_API);
			const result: ApiResponse<Category[]> = await response.json();
			if (result.success && result.data) {
				categories.set(result.data);
			}
		} catch (err) {
			console.error('Lỗi khi lấy danh mục:', err);
		}
	}

	async function submitForm(): Promise<void> {
		if (!validateForm()) return;

		// Type guard for editProductId
		if ($editMode && editProductId === null) {
			error.set('Lỗi: ID sản phẩm không hợp lệ');
			return;
		}

		loading.set(true);
		error.set('');
		message.set('');

		try {
			const url = $editMode ? `${API_BASE}/${editProductId}` : API_BASE;
			const method = $editMode ? 'PUT' : 'POST';

			const response = await fetch(url, {
				method,
				headers: {
					'Content-Type': 'application/json'
				},
				body: JSON.stringify(formData)
			});

			const result: ApiResponse<Product> = await response.json();

			if (result.success) {
				message.set(result.message);
				resetForm();
				showModal.set(false);
				fetchProducts();
			} else {
				error.set(result.message);
			}
		} catch (err) {
			error.set('Lỗi kết nối: ' + (err as Error).message);
		} finally {
			loading.set(false);
		}
	}

	async function editProduct(product: Product): Promise<void> {
		formData = {
			title: product.title,
			author: product.author || '',
			publisher: product.publisher || '',
			publicationYear: product.publicationYear || null,
			description: product.description || '',
			price: product.price || null,
			categoryIds: product.categoryIds || [],
			primaryImageUrl: product.primaryImageUrl || ''
		};
		editProductId = product.bookId;
		editMode.set(true);
		showModal.set(true);
	}

	async function deleteProduct(productId: number): Promise<void> {
		if (!confirm('Bạn có chắc chắn muốn xóa sản phẩm này?')) return;

		loading.set(true);
		try {
			const response = await fetch(`${API_BASE}/${productId}`, {
				method: 'DELETE'
			});

			const result: ApiResponse = await response.json();

			if (result.success) {
				message.set(result.message);
				fetchProducts();
			} else {
				error.set(result.message);
			}
		} catch (err) {
			error.set('Lỗi kết nối: ' + (err as Error).message);
		} finally {
			loading.set(false);
		}
	}

	function validateForm(): boolean {
		if (!formData.title.trim()) {
			error.set('Tên sản phẩm không được để trống');
			return false;
		}
		if (!formData.price || formData.price <= 0) {
			error.set('Giá sản phẩm phải lớn hơn 0');
			return false;
		}
		return true;
	}

	function resetForm(): void {
		formData = {
			title: '',
			author: '',
			publisher: '',
			publicationYear: null,
			description: '',
			price: null,
			categoryIds: [],
			primaryImageUrl: ''
		};
		editProductId = null;
		editMode.set(false);
	}

	function openAddModal(): void {
		resetForm();
		showModal.set(true);
	}

	function closeModal(): void {
		showModal.set(false);
		resetForm();
		error.set('');
	}

	function toggleCategory(categoryId: number): void {
		if (formData.categoryIds.includes(categoryId)) {
			formData.categoryIds = formData.categoryIds.filter(id => id !== categoryId);
		} else {
			formData.categoryIds = [...formData.categoryIds, categoryId];
		}
	}

	function formatPrice(price: number): string {
		return new Intl.NumberFormat('vi-VN', {
			style: 'currency',
			currency: 'VND'
		}).format(price);
	}

	// Clear messages after 3 seconds
	$: if ($message) {
		setTimeout(() => message.set(''), 3000);
	}
	$: if ($error) {
		setTimeout(() => error.set(''), 5000);
	}

	onMount(() => {
		fetchProducts();
		fetchCategories();
	});
</script>

<div class="p-6">
	<div class="flex justify-between items-center mb-6">
		<h1 class="text-3xl font-bold text-gray-900">Quản lý sản phẩm</h1>
		<button
			class="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg flex items-center gap-2"
			on:click={openAddModal}
		>
			<svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
				<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
			</svg>
			Thêm sản phẩm
		</button>
	</div>

	<!-- Messages -->
	{#if $message}
		<div class="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded mb-4">
			{$message}
		</div>
	{/if}

	{#if $error}
		<div class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
			{$error}
		</div>
	{/if}

	<!-- Loading indicator -->
	{#if $loading}
		<div class="flex justify-center py-8">
			<div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
		</div>
	{/if}

	<!-- Products table -->
	<div class="bg-white shadow-md rounded-lg overflow-hidden">
		<table class="min-w-full divide-y divide-gray-200">
			<thead class="bg-gray-50">
				<tr>
					<th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
						Sản phẩm
					</th>
					<th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
						Tác giả
					</th>
					<th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
						Giá
					</th>
					<th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
						Danh mục
					</th>
					<th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
						Thao tác
					</th>
				</tr>
			</thead>
			<tbody class="bg-white divide-y divide-gray-200">
				{#each $products as product}
					<tr>
						<td class="px-6 py-4 whitespace-nowrap">
							<div class="flex items-center">
								{#if product.primaryImageUrl}
									<img class="h-10 w-10 rounded object-cover mr-4" src={product.primaryImageUrl} alt={product.title} />
								{:else}
									<div class="h-10 w-10 rounded bg-gray-200 flex items-center justify-center mr-4">
										<svg class="w-6 h-6 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
											<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
										</svg>
									</div>
								{/if}
								<div>
									<div class="text-sm font-medium text-gray-900">{product.title}</div>
									<div class="text-sm text-gray-500">{product.publisher || ''}</div>
								</div>
							</div>
						</td>
						<td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
							{product.author || '-'}
						</td>
						<td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
							{formatPrice(product.price)}
						</td>
						<td class="px-6 py-4 whitespace-nowrap">
							<div class="flex flex-wrap gap-1">
								{#each (product.categoryNames || []) as categoryName}
									<span class="inline-flex px-2 py-1 text-xs font-semibold rounded-full bg-blue-100 text-blue-800">
										{categoryName}
									</span>
								{/each}
							</div>
						</td>
						<td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
							<div class="flex space-x-3">
								<!-- Edit Icon -->
								<button
									class="text-indigo-600 hover:text-indigo-900 p-1 rounded-md hover:bg-indigo-50 transition-colors"
									on:click={() => editProduct(product)}
									title="Sửa sản phẩm"
								>
									<svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
										<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
									</svg>
								</button>
								
								<!-- Delete Icon -->
								<button
									class="text-red-600 hover:text-red-900 p-1 rounded-md hover:bg-red-50 transition-colors"
									on:click={() => deleteProduct(product.bookId)}
									title="Xóa sản phẩm"
								>
									<svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
										<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
									</svg>
								</button>
							</div>
						</td>
					</tr>
				{/each}
			</tbody>
		</table>

		{#if $products.length === 0 && !$loading}
			<div class="text-center py-8 text-gray-500">
				Không có sản phẩm nào
			</div>
		{/if}
	</div>
</div>

<!-- Modal -->
{#if $showModal}
	<div class="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
		<div class="relative top-10 mx-auto p-5 border w-full max-w-2xl bg-white rounded-lg shadow-lg">
			<div class="flex justify-between items-center mb-4">
				<h3 class="text-lg font-bold text-gray-900">
					{$editMode ? 'Sửa sản phẩm' : 'Thêm sản phẩm mới'}
				</h3>
				<button
					class="text-gray-400 hover:text-gray-600"
					on:click={closeModal}
				>
					<svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
						<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
					</svg>
				</button>
			</div>

			<form on:submit|preventDefault={submitForm} class="space-y-4">
				<div class="grid grid-cols-1 md:grid-cols-2 gap-4">
					<div>
						<label for="title" class="block text-sm font-medium text-gray-700 mb-1">
							Tên sản phẩm *
						</label>
						<input
							id="title"
							type="text"
							bind:value={formData.title}
							class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
							required
						/>
					</div>

					<div>
						<label for="author" class="block text-sm font-medium text-gray-700 mb-1">
							Tác giả
						</label>
						<input
							id="author"
							type="text"
							bind:value={formData.author}
							class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
						/>
					</div>

					<div>
						<label for="publisher" class="block text-sm font-medium text-gray-700 mb-1">
							Nhà xuất bản
						</label>
						<input
							id="publisher"
							type="text"
							bind:value={formData.publisher}
							class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
						/>
					</div>

					<div>
						<label for="publicationYear" class="block text-sm font-medium text-gray-700 mb-1">
							Năm xuất bản
						</label>
						<input
							id="publicationYear"
							type="number"
							bind:value={formData.publicationYear}
							min="1000"
							max="2025"
							class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
						/>
					</div>

					<div>
						<label for="price" class="block text-sm font-medium text-gray-700 mb-1">
							Giá *
						</label>
						<input
							id="price"
							type="number"
							bind:value={formData.price}
							min="0"
							step="1000"
							class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
							required
						/>
					</div>

					<div>
						<label for="primaryImageUrl" class="block text-sm font-medium text-gray-700 mb-1">
							URL hình ảnh
						</label>
						<input
							id="primaryImageUrl"
							type="url"
							bind:value={formData.primaryImageUrl}
							class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
						/>
					</div>
				</div>

				<div>
					<label for="description" class="block text-sm font-medium text-gray-700 mb-1">
						Mô tả
					</label>
					<textarea
						id="description"
						bind:value={formData.description}
						rows="4"
						class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
					></textarea>
				</div>

				<div>
					<label class="block text-sm font-medium text-gray-700 mb-2">
						Danh mục
					</label>
					<div class="grid grid-cols-2 md:grid-cols-3 gap-2 max-h-32 overflow-y-auto border border-gray-300 rounded-md p-3">
						{#each $categories as category}
							<label class="flex items-center space-x-2 cursor-pointer">
								<input
									type="checkbox"
									checked={formData.categoryIds.includes(category.categoryId)}
									on:change={() => toggleCategory(category.categoryId)}
									class="rounded border-gray-300 text-blue-600 focus:ring-blue-500"
								/>
								<span class="text-sm text-gray-700">{category.categoryName}</span>
							</label>
						{/each}
					</div>
				</div>

				<div class="flex justify-end space-x-3 pt-4">
					<button
						type="button"
						on:click={closeModal}
						class="px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50"
					>
						Hủy
					</button>
					<button
						type="submit"
						disabled={$loading}
						class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:opacity-50"
					>
						{$loading ? 'Đang xử lý...' : ($editMode ? 'Cập nhật' : 'Thêm')}
					</button>
				</div>
			</form>
		</div>
	</div>
{/if}
