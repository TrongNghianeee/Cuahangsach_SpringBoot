<!-- front-end/src/routes/user/Cart/+page.svelte -->
<script lang="ts">
	import { onMount } from 'svelte';
	import { writable } from 'svelte/store';
	import type { ShoppingCartItem, ApiResponse } from '$lib/types';
	// Stores
	const cartItems = writable<ShoppingCartItem[]>([]);
	const loading = writable<boolean>(false);
	const error = writable<string>('');
	const currentUser = writable<any>(null);
	const removingItems = writable<Set<number>>(new Set());
	const itemQuantities = writable<Map<number, number>>(new Map());

	// API URLs
	const API_BASE = 'http://localhost:8080/api/user';
	const AUTH_API = 'http://localhost:8080/api/auth';

	// Get current user info
	async function getCurrentUser(): Promise<void> {
		try {
			const token = localStorage.getItem('token');
			if (!token) {
				error.set('Vui lòng đăng nhập để xem giỏ hàng');
				return;
			}

			const response = await fetch(`${AUTH_API}/me`, {
				headers: {
					'Authorization': `Bearer ${token}`,
					'Content-Type': 'application/json'
				}
			});			if (response.ok) {
				const result = await response.json();
				console.log('Current user from /me API:', result);
				// API /me trả về trực tiếp UserDTO, không có wrapper success/data
				currentUser.set(result);
				await fetchCartItems(result.userId);
			} else {
				error.set('Không thể lấy thông tin người dùng');
			}
		} catch (err) {
			console.error('Error fetching current user:', err);
			error.set('Có lỗi xảy ra khi lấy thông tin người dùng');
		}
	}

	// Fetch cart items from API
	async function fetchCartItems(userId: number): Promise<void> {
		loading.set(true);
		try {
			const token = localStorage.getItem('token');
			const response = await fetch(`${API_BASE}/shoppingcart/${userId}`, {
				headers: {
					'Authorization': `Bearer ${token}`,
					'Content-Type': 'application/json'
				}
			});
		const result: ApiResponse<ShoppingCartItem[]> = await response.json();
		if (result.success && result.data) {
			cartItems.set(result.data);
			// Initialize quantities for each item (default to 1)
			itemQuantities.update(quantities => {
				const newQuantities = new Map();
				result.data!.forEach(item => {
					newQuantities.set(item.bookId, quantities.get(item.bookId) || 1);
				});
				return newQuantities;
			});
			error.set('');
		} else {
				error.set(result.message || 'Lỗi khi lấy danh sách giỏ hàng');
			}
		} catch (err) {
			error.set('Không thể tải giỏ hàng');
			console.error('Error fetching cart items:', err);
		} finally {
			loading.set(false);
		}
	}

	// Remove item from cart
	async function removeItem(bookId: number): Promise<void> {
		const user = $currentUser;
		if (!user) return;

		// Add bookId to removing set
		removingItems.update(set => new Set(set).add(bookId));

		try {
			const token = localStorage.getItem('token');
			const response = await fetch(`${API_BASE}/shoppingcart/${bookId}`, {
				method: 'DELETE',
				headers: {
					'Authorization': `Bearer ${token}`,
					'Content-Type': 'application/json'
				},
				body: JSON.stringify({ userId: user.userId })
			});

			const result = await response.json();

			if (result.success) {
				// Refresh cart items
				await fetchCartItems(user.userId);
			} else {
				alert(result.message || 'Có lỗi xảy ra khi xóa sách khỏi giỏ hàng');
			}
		} catch (err) {
			console.error('Error removing from cart:', err);
			alert('Không thể xóa sách khỏi giỏ hàng');
		} finally {
			// Remove bookId from removing set
			removingItems.update(set => {
				const newSet = new Set(set);
				newSet.delete(bookId);
				return newSet;
			});
		}	}

	// Update item quantity
	function updateQuantity(bookId: number, newQuantity: number): void {
		if (newQuantity < 1) return;
		
		itemQuantities.update(quantities => {
			const newQuantities = new Map(quantities);
			newQuantities.set(bookId, newQuantity);
			return newQuantities;
		});
	}
	function getTotalPrice(): number {
		return $cartItems.reduce((total, item) => {
			const quantity = $itemQuantities.get(item.bookId) || 1;
			return total + (item.bookPrice * quantity);
		}, 0);
	}

	function getTotalQuantity(): number {
		return Array.from($itemQuantities.values()).reduce((total, quantity) => total + quantity, 0);
	}

	function formatPrice(price: number): string {
		return new Intl.NumberFormat('vi-VN', {
			style: 'currency',
			currency: 'VND'
		}).format(price);
	}

	onMount(async () => {
		await getCurrentUser();
	});
</script>

<svelte:head>
	<title>Giỏ hàng - BookStore</title>
</svelte:head>

<div class="max-w-7xl mx-auto py-8 px-4 sm:px-6 lg:px-8">
	<!-- Page Header -->
	<div class="mb-8">
		<h1 class="text-3xl font-bold text-gray-900">Giỏ hàng của bạn</h1>
		<p class="mt-2 text-gray-600">Quản lý các sản phẩm bạn muốn mua</p>
	</div>

	{#if $loading}
		<!-- Loading state -->
		<div class="text-center py-12">
			<svg class="animate-spin -ml-1 mr-3 h-8 w-8 text-blue-600 mx-auto" fill="none" viewBox="0 0 24 24">
				<circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
				<path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
			</svg>
			<p class="mt-2 text-gray-600">Đang tải giỏ hàng...</p>
		</div>
	{:else if $error}
		<!-- Error state -->
		<div class="text-center py-12">
			<svg class="mx-auto h-12 w-12 text-red-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
				<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L5.082 16.5c-.77.833.192 2.5 1.732 2.5z"/>
			</svg>
			<h3 class="mt-2 text-sm font-medium text-gray-900">Có lỗi xảy ra</h3>
			<p class="mt-1 text-sm text-gray-500">{$error}</p>
			<div class="mt-6">
				<a href="/auth/login" class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700">
					Đăng nhập
				</a>
			</div>
		</div>
	{:else if $cartItems.length === 0}
		<!-- Empty cart -->
		<div class="text-center py-12">
			<svg class="mx-auto h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
				<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 3h2l.4 2M7 13h10l4-8H5.4m1.6 8L5 3H3m4 10a1 1 0 100 2 1 1 0 000-2zm10 0a1 1 0 100 2 1 1 0 000-2z"/>
			</svg>
			<h3 class="mt-2 text-sm font-medium text-gray-900">Giỏ hàng trống</h3>
			<p class="mt-1 text-sm text-gray-500">Bạn chưa có sản phẩm nào trong giỏ hàng.</p>
			<div class="mt-6">
				<a href="/user" class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700">
					Tiếp tục mua sắm
				</a>
			</div>
		</div>
	{:else}
		<div class="lg:grid lg:grid-cols-12 lg:gap-x-8">			<!-- Cart items -->
			<div class="lg:col-span-8">
				<div class="bg-white shadow overflow-hidden sm:rounded-md">
					<ul class="divide-y divide-gray-200">
						{#each $cartItems as item (item.bookId)}
							<li class="px-6 py-6">
								<div class="flex items-start space-x-4">
									<!-- Book image placeholder -->
									<div class="flex-shrink-0 w-20 h-28 bg-gray-200 rounded-md flex items-center justify-center">
										<svg class="w-8 h-8 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
											<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.746 0 3.332.477 4.5 1.253v13C19.832 18.477 18.246 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"/>
										</svg>
									</div>									<!-- Book info -->
									<div class="flex-1 min-w-0">
										<h3 class="text-lg font-medium text-gray-900">{item.bookTitle}</h3>
										<p class="text-sm text-gray-500 mt-1">Tác giả: {item.bookAuthor || 'Không xác định'}</p>
										{#if item.bookPublisher}
											<p class="text-sm text-gray-500">Nhà xuất bản: {item.bookPublisher}</p>
										{/if}
										
										<!-- Quantity selector -->
										<div class="mt-3 flex items-center space-x-3">
											<span class="text-sm text-gray-600">Số lượng:</span>
											<div class="flex items-center border border-gray-300 rounded-md">
												<button 
													on:click={() => updateQuantity(item.bookId, ($itemQuantities.get(item.bookId) || 1) - 1)}
													disabled={($itemQuantities.get(item.bookId) || 1) <= 1}
													class="px-2 py-1 text-gray-600 hover:text-gray-800 disabled:opacity-50 disabled:cursor-not-allowed"
												>
													<svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
														<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 12H4"/>
													</svg>
												</button>
												<span class="px-3 py-1 text-sm font-medium min-w-[2rem] text-center">
													{$itemQuantities.get(item.bookId) || 1}
												</span>
												<button 
													on:click={() => updateQuantity(item.bookId, ($itemQuantities.get(item.bookId) || 1) + 1)}
													class="px-2 py-1 text-gray-600 hover:text-gray-800"
												>
													<svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
														<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"/>
													</svg>
												</button>
											</div>
										</div>

										<div class="mt-2 flex items-center justify-between">
											<div>
												<p class="text-lg font-semibold text-blue-600">{formatPrice(item.bookPrice)}</p>
												{#if ($itemQuantities.get(item.bookId) || 1) > 1}
													<p class="text-sm text-gray-500">
														Tổng: {formatPrice(item.bookPrice * ($itemQuantities.get(item.bookId) || 1))}
													</p>
												{/if}
											</div>
											
											<!-- Remove button -->
											<button 
												on:click={() => removeItem(item.bookId)}
												disabled={$removingItems.has(item.bookId)}
												class="inline-flex items-center px-3 py-1 border border-red-300 rounded-md text-sm text-red-700 bg-red-50 hover:bg-red-100 disabled:opacity-50 disabled:cursor-not-allowed"
											>
												{#if $removingItems.has(item.bookId)}
													<svg class="w-3 h-3 animate-spin mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
														<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"/>
													</svg>
													Đang xóa...
												{:else}
													<svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
														<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/>
													</svg>
													Xóa
												{/if}
											</button>
										</div>
									</div>
								</div>
							</li>						{/each}
					</ul>
				</div>
			</div>

			<!-- Order summary -->
			<div class="lg:col-span-4 mt-8 lg:mt-0">
				<div class="bg-white rounded-lg shadow-md p-6 sticky top-24">
					<h2 class="text-lg font-medium text-gray-900 mb-4">Tóm tắt đơn hàng</h2>
							<div class="space-y-3">
						<div class="flex justify-between text-sm">
							<span class="text-gray-600">Tạm tính ({getTotalQuantity()} sản phẩm)</span>
							<span class="font-medium">{formatPrice(getTotalPrice())}</span>
						</div>
						<div class="flex justify-between text-sm">
							<span class="text-gray-600">Phí vận chuyển</span>
							<span class="font-medium text-green-600">Miễn phí</span>
						</div>
						<div class="border-t border-gray-200 pt-3">
							<div class="flex justify-between text-base font-medium text-gray-900">
								<span>Tổng cộng</span>
								<span class="text-lg">{formatPrice(getTotalPrice())}</span>
							</div>
						</div>
					</div>

					<div class="mt-6 space-y-3">
						<button 
							class="w-full bg-blue-600 text-white py-3 px-4 rounded-md hover:bg-blue-700 transition-colors duration-200 font-medium"
							disabled
							title="Chức năng thanh toán chưa được triển khai"
						>
							Thanh toán
						</button>
						<a href="/user" class="block w-full text-center py-3 px-4 border border-gray-300 rounded-md text-sm font-medium text-gray-700 hover:bg-gray-50 transition-colors duration-200">
							Tiếp tục mua sắm
						</a>
					</div>
				</div>
			</div>
		</div>
	{/if}
</div>
