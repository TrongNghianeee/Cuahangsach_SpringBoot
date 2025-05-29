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
	const showProductModal = writable<boolean>(false);
	const showCategoryModal = writable<boolean>(false);
	const editMode = writable<boolean>(false);
	const editCategoryMode = writable<boolean>(false);
	const selectedProducts = writable<Set<number>>(new Set());
	const showInventoryModal = writable<boolean>(false);

	// Form data
	let formData: ProductFormData = {
		title: '',
		author: '',
		publisher: '',
		publicationYear: null,
		description: '',
		price: null,
		stockQuantity: 0,
		categoryIds: []
	};
	let categoryFormData = {
		categoryName: ''
	};

	// Inventory form data
	let inventoryFormData: Array<{
		bookId: number;
		title: string;
		stockQuantity: number;
		quantity: number;
		price: number;
	}> = [];

	let editProductId: number | null = null;
	let editCategoryId: number | null = null;

	// API Base URLs
	// API Base URLs
	const API_BASE = 'http://localhost:8080/api/admin/products';
	const CATEGORIES_API = 'http://localhost:8080/api/admin/categories';

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

	async function submitProductForm(): Promise<void> {
		if (!validateProductForm()) return;

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
				resetProductForm();
				showProductModal.set(false);
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

	async function submitCategoryForm(): Promise<void> {
		if (!validateCategoryForm()) return;

		if ($editCategoryMode && editCategoryId === null) {
			error.set('Lỗi: ID danh mục không hợp lệ');
			return;
		}

		loading.set(true);
		error.set('');
		message.set('');

		try {
			const url = $editCategoryMode ? `${CATEGORIES_API}/${editCategoryId}` : CATEGORIES_API;
			const method = $editCategoryMode ? 'PUT' : 'POST';

			const response = await fetch(url, {
				method,
				headers: {
					'Content-Type': 'application/json'
				},
				body: JSON.stringify(categoryFormData)
			});

			const result: ApiResponse<Category> = await response.json();

			if (result.success) {
				message.set(result.message);
				resetCategoryForm();
				showCategoryModal.set(false);
				fetchCategories();
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
			stockQuantity: product.stockQuantity || 0,
			categoryIds: product.categories?.map(c => c.categoryId) || []
		};
		editProductId = product.bookId;
		editMode.set(true);
		showProductModal.set(true);
	}

	async function editCategory(category: Category): Promise<void> {
		categoryFormData = {
			categoryName: category.categoryName
		};
		editCategoryId = category.categoryId;
		editCategoryMode.set(true);
		showCategoryModal.set(true);
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

	async function deleteCategory(categoryId: number): Promise<void> {
		if (!confirm('Bạn có chắc chắn muốn xóa danh mục này?')) return;

		loading.set(true);
		try {
			const response = await fetch(`${CATEGORIES_API}/${categoryId}`, {
				method: 'DELETE'
			});

			const result: ApiResponse = await response.json();

			if (result.success) {
				message.set(result.message);
				fetchCategories();
			} else {
				error.set(result.message);
			}
		} catch (err) {
			error.set('Lỗi kết nối: ' + (err as Error).message);
		} finally {
			loading.set(false);
		}
	}

	function validateProductForm(): boolean {
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

	function validateCategoryForm(): boolean {
		if (!categoryFormData.categoryName.trim()) {
			error.set('Tên danh mục không được để trống');
			return false;
		}
		return true;
	}

	function resetProductForm(): void {
		formData = {
			title: '',
			author: '',
			publisher: '',
			publicationYear: null,
			description: '',
			price: null,
			stockQuantity: 0,
			categoryIds: []
		};
		editProductId = null;
		editMode.set(false);
	}

	function resetCategoryForm(): void {
		categoryFormData = {
			categoryName: ''
		};
		editCategoryId = null;
		editCategoryMode.set(false);
	}

	function openAddProductModal(): void {
		resetProductForm();
		showProductModal.set(true);
	}

	function openAddCategoryModal(): void {
		resetCategoryForm();
		showCategoryModal.set(true);
	}

	function closeProductModal(): void {
		showProductModal.set(false);
		resetProductForm();
		error.set('');
	}

	function closeCategoryModal(): void {
		showCategoryModal.set(false);
		resetCategoryForm();
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
	function getCategoryNames(product: Product): string {
		if (!product.categories || product.categories.length === 0) return 'Chưa phân loại';
		return product.categories.map(c => c.categoryName).join(', ');
	}

	// Inventory functions
	function toggleProductSelection(productId: number): void {
		selectedProducts.update(selected => {
			const newSelected = new Set(selected);
			if (newSelected.has(productId)) {
				newSelected.delete(productId);
			} else {
				newSelected.add(productId);
			}
			return newSelected;
		});
	}

	function openInventoryModal(): void {
		const selected = $selectedProducts;
		const selectedProductsData = $products.filter(product => selected.has(product.bookId));
		
		inventoryFormData = selectedProductsData.map(product => ({
			bookId: product.bookId,
			title: product.title,
			stockQuantity: product.stockQuantity || 0,
			quantity: 0,
			price: 0
		}));

		showInventoryModal.set(true);
	}

	function closeInventoryModal(): void {
		showInventoryModal.set(false);
		inventoryFormData = [];
		selectedProducts.set(new Set());
		error.set('');
	}

	function handleImport(): void {
		// Logic for import will be implemented later
		console.log('Import data:', inventoryFormData);
	}

	function handleExport(): void {
		// Logic for export will be implemented later
		console.log('Export data:', inventoryFormData);
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

<div class="container mx-auto px-4 py-8">
	<div class="mb-8">
		<h1 class="text-3xl font-bold text-gray-900 mb-2">Quản lý Sản phẩm</h1>
		<p class="text-gray-600">Quản lý sản phẩm và danh mục trong cửa hàng</p>
	</div>

	<!-- Alert Messages -->
	{#if $message}
		<div class="mb-4 p-4 bg-green-100 border border-green-400 text-green-700 rounded">
			{$message}
		</div>
	{/if}

	{#if $error}
		<div class="mb-4 p-4 bg-red-100 border border-red-400 text-red-700 rounded">
			{$error}
		</div>
	{/if}
	<!-- Action Buttons -->
	<div class="mb-6 flex gap-4">
		<button
			on:click={openAddProductModal}
			class="bg-blue-600 hover:bg-blue-700 text-white font-medium px-4 py-2 rounded-lg flex items-center gap-2"
		>
			<svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
				<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"/>
			</svg>
			Thêm Sản phẩm
		</button>
		
		<button
			on:click={openAddCategoryModal}
			class="bg-green-600 hover:bg-green-700 text-white font-medium px-4 py-2 rounded-lg flex items-center gap-2"
		>
			<svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
				<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.99 1.99 0 013 12V7a4 4 0 014-4z"/>
			</svg>
			Thêm Danh mục
		</button>

		<button
			on:click={openInventoryModal}
			disabled={$selectedProducts.size === 0}
			class="bg-purple-600 hover:bg-purple-700 text-white font-medium px-4 py-2 rounded-lg flex items-center gap-2 disabled:opacity-50 disabled:cursor-not-allowed"
		>
			<svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
				<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"/>
			</svg>
			Quản lý Tồn kho ({$selectedProducts.size})
		</button>
	</div>

	<!-- Categories Section -->
	<div class="mb-8 bg-white rounded-lg shadow p-6">
		<h2 class="text-xl font-semibold mb-4">Danh mục hiện có</h2>
		<div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
			{#each $categories as category}


				<div class="border rounded-lg p-4 flex justify-between items-center">
					<span class="font-medium">{category.categoryName}</span>
					<div class="flex gap-2">
						<button
							on:click={() => editCategory(category)}
							class="text-blue-600 hover:text-blue-800 text-sm"
						>
							Sửa
						</button>
						<button
							on:click={() => deleteCategory(category.categoryId)}
							class="text-red-600 hover:text-red-800 text-sm"
						>
							Xóa
						</button>
					</div>
				</div>
			{/each}
		</div>
	</div>

	<!-- Products Table -->
	<div class="bg-white rounded-lg shadow overflow-hidden">
		<div class="px-6 py-4 border-b border-gray-200">
			<h2 class="text-xl font-semibold">Danh sách Sản phẩm</h2>
		</div>

		{#if $loading}
			<div class="flex justify-center items-center py-8">
				<div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
			</div>		{:else}
			<div class="overflow-x-auto">
				<table class="min-w-full divide-y divide-gray-200">
					<thead class="bg-gray-50">
						<tr>
							<th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">								<input
									type="checkbox"
									class="rounded border-gray-300 text-blue-600 shadow-sm focus:border-blue-300 focus:ring focus:ring-blue-200 focus:ring-opacity-50"
									checked={$selectedProducts.size === $products.length && $products.length > 0}
									on:change={(e) => {
										const target = e.target as HTMLInputElement;
										if (target.checked) {
											selectedProducts.set(new Set($products.map(p => p.bookId)));
										} else {
											selectedProducts.set(new Set());
										}
									}}
								/>
							</th>
							<th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
								ID
							</th>
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
								Tồn kho
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
							<tr class="hover:bg-gray-50">
								<td class="px-6 py-4 whitespace-nowrap">
									<input
										type="checkbox"
										class="rounded border-gray-300 text-blue-600 shadow-sm focus:border-blue-300 focus:ring focus:ring-blue-200 focus:ring-opacity-50"
										checked={$selectedProducts.has(product.bookId)}
										on:change={() => toggleProductSelection(product.bookId)}
									/>
								</td>
								<td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
									{product.bookId}
								</td>
								<td class="px-6 py-4 whitespace-nowrap">
									<div>
										<div class="text-sm font-medium text-gray-900">{product.title}</div>
										<div class="text-sm text-gray-500">{product.publisher || 'Chưa có NXB'}</div>
									</div>
								</td>
								<td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
									{product.author || 'Chưa có tác giả'}
								</td>
								<td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
									{product.price ? formatPrice(product.price) : 'Chưa có giá'}
								</td>
								<td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
									{product.stockQuantity || 0}
								</td>
								<td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
									{getCategoryNames(product)}
								</td>
								<td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
									<button
										on:click={() => editProduct(product)}
										class="text-blue-600 hover:text-blue-900 mr-3"
									>
										Sửa
									</button>
									<button
										on:click={() => deleteProduct(product.bookId)}
										class="text-red-600 hover:text-red-900"
									>
										Xóa
									</button>
								</td>
							</tr>						{/each}
					</tbody>
				</table>
			</div>
		{/if}
	</div>
</div>

<!-- Product Modal -->
{#if $showProductModal}
	<div class="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
		<div class="relative top-20 mx-auto p-5 border w-11/12 md:w-3/4 lg:w-1/2 shadow-lg rounded-md bg-white">
			<div class="mt-3">
				<div class="flex justify-between items-center mb-4">
					<h3 class="text-lg font-medium text-gray-900">
						{$editMode ? 'Chỉnh sửa' : 'Thêm'} Sản phẩm
					</h3>
					<button
						on:click={closeProductModal}
						class="text-gray-400 hover:text-gray-600"
					>
						<svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
							<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
						</svg>
					</button>
				</div>

				<form on:submit|preventDefault={submitProductForm} class="space-y-4">
					{#if $error}
						<div class="p-4 bg-red-100 border border-red-400 text-red-700 rounded">
							{$error}
						</div>
					{/if}

					{#if $message}
						<div class="p-4 bg-green-100 border border-green-400 text-green-700 rounded">
							{$message}
						</div>
					{/if}

					<div class="grid grid-cols-1 md:grid-cols-2 gap-4">
						<div>
							<label class="block text-sm font-medium text-gray-700 mb-1">
								Tên sách <span class="text-red-500">*</span>
							</label>
							<input
								type="text"
								bind:value={formData.title}
								class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
								required
							/>
						</div>

						<div>
							<label class="block text-sm font-medium text-gray-700 mb-1">
								Tác giả
							</label>
							<input
								type="text"
								bind:value={formData.author}
								class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
							/>
						</div>

						<div>
							<label class="block text-sm font-medium text-gray-700 mb-1">
								Nhà xuất bản
							</label>
							<input
								type="text"
								bind:value={formData.publisher}
								class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
							/>
						</div>

						<div>
							<label class="block text-sm font-medium text-gray-700 mb-1">
								Năm xuất bản
							</label>
							<input
								type="number"
								bind:value={formData.publicationYear}
								min="1900"
								max="2030"
								class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
							/>
						</div>

						<div>
							<label class="block text-sm font-medium text-gray-700 mb-1">
								Giá <span class="text-red-500">*</span>
							</label>
							<input
								type="number"
								bind:value={formData.price}
								min="0"
								step="1000"
								class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
								required
							/>
						</div>

						<div>
							<label class="block text-sm font-medium text-gray-700 mb-1">
								Số lượng tồn kho
							</label>
							<input
								type="number"
								bind:value={formData.stockQuantity}
								min="0"
								class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
							/>
						</div>
					</div>

					<div>
						<label class="block text-sm font-medium text-gray-700 mb-1">
							Mô tả
						</label>
						<textarea
							bind:value={formData.description}
							rows="3"
							class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
						></textarea>
					</div>

					<div>
						<label class="block text-sm font-medium text-gray-700 mb-2">
							Danh mục
						</label>
						<div class="grid grid-cols-2 md:grid-cols-3 gap-2">
							{#each $categories as category}
								<label class="flex items-center">
									<input
										type="checkbox"
										checked={formData.categoryIds.includes(category.categoryId)}
										on:change={() => toggleCategory(category.categoryId)}
										class="mr-2"
									/>
									<span class="text-sm">{category.categoryName}</span>
								</label>
							{/each}
						</div>
					</div>

					<div class="flex justify-end space-x-3 pt-4">
						<button
							type="button"
							on:click={closeProductModal}
							class="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-200 hover:bg-gray-300 rounded-md"
						>
							Hủy
						</button>
						<button
							type="submit"
							disabled={$loading}
							class="px-4 py-2 text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 rounded-md disabled:opacity-50"
						>
							{$loading ? 'Đang xử lý...' : ($editMode ? 'Cập nhật' : 'Thêm')}
						</button>
					</div>
				</form>
			</div>
		</div>
	</div>
{/if}

<!-- Category Modal -->
{#if $showCategoryModal}
	<div class="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
		<div class="relative top-20 mx-auto p-5 border w-11/12 md:w-1/2 lg:w-1/3 shadow-lg rounded-md bg-white">
			<div class="mt-3">
				<div class="flex justify-between items-center mb-4">
					<h3 class="text-lg font-medium text-gray-900">
						{$editCategoryMode ? 'Chỉnh sửa' : 'Thêm'} Danh mục
					</h3>
					<button
						on:click={closeCategoryModal}
						class="text-gray-400 hover:text-gray-600"
					>
						<svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
							<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
						</svg>
					</button>
				</div>

				<form on:submit|preventDefault={submitCategoryForm} class="space-y-4">
					{#if $error}
						<div class="p-4 bg-red-100 border border-red-400 text-red-700 rounded">
							{$error}
						</div>
					{/if}

					{#if $message}
						<div class="p-4 bg-green-100 border border-green-400 text-green-700 rounded">
							{$message}
						</div>
					{/if}

					<div>
						<label class="block text-sm font-medium text-gray-700 mb-1">
							Tên danh mục <span class="text-red-500">*</span>
						</label>
						<input
							type="text"
							bind:value={categoryFormData.categoryName}
							class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
							required
						/>
					</div>					<div class="flex justify-end space-x-3 pt-4">
						<button
							type="button"
							on:click={closeCategoryModal}
							class="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-200 hover:bg-gray-300 rounded-md"
						>
							Hủy
						</button>
						<button
							type="submit"
							disabled={$loading}
							class="px-4 py-2 text-sm font-medium text-white bg-green-600 hover:bg-green-700 rounded-md disabled:opacity-50"
						>
							{$loading ? 'Đang xử lý...' : ($editCategoryMode ? 'Cập nhật' : 'Thêm')}
						</button>
					</div>
				</form>
			</div>
		</div>
	</div>
{/if}

<!-- Inventory Modal -->
{#if $showInventoryModal}
	<div class="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
		<div class="relative top-20 mx-auto p-5 border w-11/12 md:w-4/5 lg:w-3/4 shadow-lg rounded-md bg-white">
			<div class="mt-3">
				<div class="flex justify-between items-center mb-4">
					<h3 class="text-lg font-medium text-gray-900">
						Quản lý Tồn kho
					</h3>
					<button
						on:click={closeInventoryModal}
						class="text-gray-400 hover:text-gray-600"
					>
						<svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
							<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
						</svg>
					</button>
				</div>

				{#if $error}
					<div class="p-4 bg-red-100 border border-red-400 text-red-700 rounded mb-4">
						{$error}
					</div>
				{/if}

				{#if inventoryFormData.length > 0}
					<div class="overflow-x-auto">
						<table class="min-w-full divide-y divide-gray-200">
							<thead class="bg-gray-50">
								<tr>
									<th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
										ID
									</th>
									<th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
										Tên
									</th>
									<th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
										Tồn kho
									</th>
									<th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
										Số lượng
									</th>
									<th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
										Giá
									</th>
								</tr>
							</thead>
							<tbody class="bg-white divide-y divide-gray-200">
								{#each inventoryFormData as item, index}
									<tr class="hover:bg-gray-50">
										<td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
											{item.bookId}
										</td>
										<td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
											{item.title}
										</td>
										<td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
											{item.stockQuantity}
										</td>
										<td class="px-6 py-4 whitespace-nowrap">
											<input
												type="number"
												bind:value={inventoryFormData[index].quantity}
												min="0"
												class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
												placeholder="Nhập số lượng"
											/>
										</td>
										<td class="px-6 py-4 whitespace-nowrap">
											<input
												type="number"
												bind:value={inventoryFormData[index].price}
												min="0"
												step="1000"
												class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
												placeholder="Nhập giá"
											/>
										</td>
									</tr>
								{/each}
							</tbody>
						</table>
					</div>

					<div class="flex justify-end space-x-3 pt-6">
						<button
							type="button"
							on:click={closeInventoryModal}
							class="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-200 hover:bg-gray-300 rounded-md"
						>
							Thoát
						</button>
						<button
							type="button"
							on:click={handleImport}
							class="px-4 py-2 text-sm font-medium text-white bg-green-600 hover:bg-green-700 rounded-md"
						>
							Nhập
						</button>
						<button
							type="button"
							on:click={handleExport}
							class="px-4 py-2 text-sm font-medium text-white bg-red-600 hover:bg-red-700 rounded-md"
						>
							Xuất
						</button>
					</div>
				{:else}
					<div class="text-center py-8">
						<p class="text-gray-500">Không có sản phẩm nào được chọn</p>
					</div>
				{/if}
			</div>
		</div>
	</div>
{/if}
