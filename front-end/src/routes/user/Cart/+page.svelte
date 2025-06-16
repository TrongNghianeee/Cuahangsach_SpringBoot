<!-- front-end/src/routes/user/Cart/+page.svelte -->
<script lang="ts">
	import { onMount } from 'svelte';
	import { writable } from 'svelte/store';
	import type { ShoppingCartItem, ApiResponse, BookImage } from '$lib/types';
	import { getUserPrimaryImage } from '$lib/userImageService';
	import { getImageUrl } from '$lib/imageUtils';
	// Stores
	const cartItems = writable<ShoppingCartItem[]>([]);
	const loading = writable<boolean>(false);
	const error = writable<string>('');	const currentUser = writable<any>(null);	const removingItems = writable<Set<number>>(new Set());
	const itemQuantities = writable<Map<number, number>>(new Map());
	const bookImages = writable<Map<number, BookImage | null>>(new Map());
	
	// Checkout form data
	const shippingAddress = writable<string>('');
	const paymentMethod = writable<string>('COD'); // Default to Cash on Delivery

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
				
				// Auto-fill shipping address if user has address
				if (result.address) {
					shippingAddress.set(result.address);
				}
				
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
			});		const result: ApiResponse<ShoppingCartItem[]> = await response.json();
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
			
			// Load images for each cart item
			await loadCartItemImages(result.data);
			
			error.set('');
		} else {
				error.set(result.message || 'Lỗi khi lấy danh sách giỏ hàng');
			}
		} catch (err) {
			error.set('Không thể tải giỏ hàng');
			console.error('Error fetching cart items:', err);
		} finally {
			loading.set(false);
		}	}

	// Load images for cart items
	async function loadCartItemImages(items: ShoppingCartItem[]): Promise<void> {
		const imagePromises = items.map(async (item) => {
			try {
				const primaryImage = await getUserPrimaryImage(item.bookId);
				bookImages.update(images => {
					const newImages = new Map(images);
					newImages.set(item.bookId, primaryImage);
					return newImages;
				});
			} catch (error) {
				console.warn(`Failed to load image for book ${item.bookId}:`, error);
				bookImages.update(images => {
					const newImages = new Map(images);
					newImages.set(item.bookId, null);
					return newImages;
				});
			}
		});

		// Wait for all image loads to complete (or fail)
		await Promise.allSettled(imagePromises);
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
	// Handle checkout
	async function handleCheckout(): Promise<void> {
		if (!$shippingAddress.trim()) {
			alert('Vui lòng nhập địa chỉ giao hàng');
			return;
		}

		if ($cartItems.length === 0) {
			alert('Giỏ hàng trống');
			return;
		}

		// Show confirmation dialog first
		const confirmMessage = `Xác nhận đặt hàng:\n\n` +
			`Tổng tiền: ${formatPrice(getTotalPrice())}\n` +
			`Địa chỉ giao hàng: ${$shippingAddress}\n` +
			`Phương thức thanh toán: ${$paymentMethod === 'COD' ? 'Tiền mặt khi nhận hàng' : 'Chuyển khoản ngân hàng'}\n` +
			`Số lượng sản phẩm: ${getTotalQuantity()}\n\n` +
			`Bạn có muốn tiếp tục?`;
		
		if (!confirm(confirmMessage)) {
			return;
		}

		// Prepare checkout data
		const checkoutData = {
			userId: $currentUser.userId,
			totalAmount: getTotalPrice(),
			shippingAddress: $shippingAddress.trim(),
			paymentMethod: $paymentMethod,
			items: $cartItems.map(item => ({
				bookId: item.bookId,
				quantity: $itemQuantities.get(item.bookId) || 1,
				price: item.bookPrice,
				bookTitle: item.bookTitle,
				bookAuthor: item.bookAuthor
			}))
		};

		console.log('Checkout data to be sent:', checkoutData);
		
		try {
			loading.set(true);
			const token = localStorage.getItem('token');
			const response = await fetch(`${API_BASE}/payment/checkout`, {
				method: 'POST',
				headers: {
					'Authorization': `Bearer ${token}`,
					'Content-Type': 'application/json'
				},
				body: JSON.stringify(checkoutData)
			});

			const result = await response.json();

			if (result.success) {
				alert('Đặt hàng thành công! Cảm ơn bạn đã mua hàng.');
				console.log('Order created:', result.data);
				
				// Clear cart items from state
				cartItems.set([]);
				itemQuantities.set(new Map());
				
				// Optionally redirect to order history or confirmation page
				// window.location.href = '/user/orders';
			} else {
				alert(`Đặt hàng thất bại: ${result.message}`);
			}
		} catch (err) {
			console.error('Error during checkout:', err);
			alert('Có lỗi xảy ra khi đặt hàng. Vui lòng thử lại.');
		} finally {
			loading.set(false);
		}
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
							<li class="px-6 py-6">								<div class="flex items-start space-x-4">									<!-- Book image -->
									<div class="book-image-container flex-shrink-0 w-20 h-28 rounded-md overflow-hidden flex items-center justify-center relative">
										{#if $bookImages.has(item.bookId)}
											{#if $bookImages.get(item.bookId)}
												<img 
													src={getImageUrl($bookImages.get(item.bookId)?.imageUrl || '')}
													alt={item.bookTitle}
													class="book-image w-full h-full object-contain"
													loading="lazy"
													on:error={(e) => {
														const target = e.target as HTMLImageElement;
														target.style.display = 'none';
														const placeholder = target.nextElementSibling as HTMLElement;
														if (placeholder) {
															placeholder.style.display = 'flex';
														}
													}}
												/>
												<!-- Fallback placeholder for failed image loads (hidden by default) -->
												<div class="hidden w-full h-full items-center justify-center bg-gray-200 absolute inset-0">
													<svg class="w-6 h-6 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
														<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
													</svg>
												</div>
											{:else}
												<!-- No image available -->
												<div class="w-full h-full flex items-center justify-center bg-gray-200">
													<div class="text-center">
														<svg class="w-6 h-6 text-gray-400 mx-auto mb-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
															<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
														</svg>
														<p class="text-xs text-gray-400">Không có ảnh</p>
													</div>
												</div>
											{/if}
										{:else}
											<!-- Loading state -->
											<div class="w-full h-full flex items-center justify-center bg-gray-100">
												<div class="text-center">
													<svg class="w-5 h-5 text-gray-400 mx-auto mb-1 animate-pulse" fill="none" stroke="currentColor" viewBox="0 0 24 24">
														<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
													</svg>
													<p class="text-xs text-gray-400">Đang tải...</p>
												</div>
											</div>
										{/if}
									</div><!-- Book info -->
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
						</div>					</div>

					<!-- Shipping Address Section -->
					<div class="mt-6 border-t border-gray-200 pt-6">
						<h3 class="text-sm font-medium text-gray-900 mb-3">Địa chỉ giao hàng</h3>
						<div class="space-y-3">
							<textarea
								bind:value={$shippingAddress}
								placeholder="Nhập địa chỉ giao hàng của bạn..."
								rows="3"
								class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none text-sm"
								required
							></textarea>
							{#if $currentUser && $currentUser.address && $shippingAddress !== $currentUser.address}
								<button
									type="button"
									on:click={() => shippingAddress.set($currentUser.address)}
									class="text-xs text-blue-600 hover:text-blue-800 underline"
								>
									Sử dụng địa chỉ mặc định
								</button>
							{/if}
						</div>
					</div>

					<!-- Payment Method Section -->
					<div class="mt-6 border-t border-gray-200 pt-6">
						<h3 class="text-sm font-medium text-gray-900 mb-3">Phương thức thanh toán</h3>
						<div class="space-y-3">
							<label class="flex items-center space-x-3 cursor-pointer">
								<input
									type="radio"
									bind:group={$paymentMethod}
									value="COD"
									class="form-radio h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300"
								/>
								<div class="flex items-center space-x-2">
									<svg class="w-5 h-5 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
										<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 9V7a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2m2 4h10a2 2 0 002-2v-6a2 2 0 00-2-2H9a2 2 0 00-2 2v6a2 2 0 002 2zm7-5a2 2 0 11-4 0 2 2 0 014 0z"/>
									</svg>
									<span class="text-sm font-medium">Tiền mặt khi nhận hàng (COD)</span>
								</div>
							</label>
							
							<label class="flex items-center space-x-3 cursor-pointer">
								<input
									type="radio"
									bind:group={$paymentMethod}
									value="Bank Transfer"
									class="form-radio h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300"
								/>
								<div class="flex items-center space-x-2">
									<svg class="w-5 h-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
										<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z"/>
									</svg>
									<span class="text-sm font-medium">Chuyển khoản ngân hàng</span>
								</div>
							</label>
						</div>
					</div>					<div class="mt-6 space-y-3">
						<button 
							class="w-full bg-blue-600 text-white py-3 px-4 rounded-md hover:bg-blue-700 transition-colors duration-200 font-medium disabled:bg-gray-400 disabled:cursor-not-allowed"
							disabled={!$shippingAddress.trim()}
							on:click={handleCheckout}
							title={!$shippingAddress.trim() ? "Vui lòng nhập địa chỉ giao hàng" : "Tiến hành thanh toán"}
						>
							{#if !$shippingAddress.trim()}
								Vui lòng nhập địa chỉ giao hàng
							{:else}
								Thanh toán ({formatPrice(getTotalPrice())})
							{/if}
						</button>
						<a href="/user" class="block w-full text-center py-3 px-4 border border-gray-300 rounded-md text-sm font-medium text-gray-700 hover:bg-gray-50 transition-colors duration-200">
							Tiếp tục mua sắm
						</a>
					</div>
				</div>
			</div>		</div>
	{/if}
</div>

<style>
	/* Custom radio button styling */
	.form-radio {
		appearance: none;
		width: 1rem;
		height: 1rem;
		border: 2px solid #d1d5db;
		border-radius: 50%;
		background-color: white;
		position: relative;
		cursor: pointer;
	}
	
	.form-radio:checked {
		border-color: #2563eb;
		background-color: #2563eb;
	}
	
	.form-radio:checked::after {
		content: '';
		position: absolute;
		top: 50%;
		left: 50%;
		transform: translate(-50%, -50%);
		width: 0.25rem;
		height: 0.25rem;
		border-radius: 50%;
		background-color: white;
	}
	
	.form-radio:focus {
		outline: none;
		box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
	}

	/* Book image styling */
	.book-image-container {
		position: relative;
		background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
		border: 1px solid #e2e8f0;
		transition: all 0.2s ease-in-out;
	}

	.book-image-container:hover {
		box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
	}

	.book-image {
		transition: transform 0.2s ease-in-out;
	}

	.book-image:hover {
		transform: scale(1.02);
	}

	/* Loading animation */
	@keyframes shimmer {
		0% {
			background-position: -200% 0;
		}
		100% {
			background-position: 200% 0;
		}
	}

	.image-loading {
		background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
		background-size: 200% 100%;
		animation: shimmer 1.5s infinite;
	}
</style>
