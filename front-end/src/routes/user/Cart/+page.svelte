<!-- front-end/src/routes/user/Cart/+page.svelte -->
<script lang="ts">
	// Mock cart data for demonstration
	let cartItems = [
		{ id: 1, title: "Sách Lập trình Web", author: "Nguyễn Văn A", price: 250000, quantity: 2, image: null },
		{ id: 2, title: "Cơ sở dữ liệu nâng cao", author: "Trần Thị B", price: 180000, quantity: 1, image: null },
		{ id: 3, title: "Thuật toán và cấu trúc dữ liệu", author: "Lê Văn C", price: 320000, quantity: 1, image: null }
	];

	function updateQuantity(id: number, newQuantity: number) {
		if (newQuantity <= 0) {
			removeItem(id);
		} else {
			cartItems = cartItems.map(item => 
				item.id === id ? { ...item, quantity: newQuantity } : item
			);
		}
	}

	function removeItem(id: number) {
		cartItems = cartItems.filter(item => item.id !== id);
	}

	function getTotalPrice() {
		return cartItems.reduce((total, item) => total + (item.price * item.quantity), 0);
	}

	function formatPrice(price: number) {
		return new Intl.NumberFormat('vi-VN', {
			style: 'currency',
			currency: 'VND'
		}).format(price);
	}
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

	{#if cartItems.length === 0}
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
		<div class="lg:grid lg:grid-cols-12 lg:gap-x-8">
			<!-- Cart items -->
			<div class="lg:col-span-8">
				<div class="bg-white shadow overflow-hidden sm:rounded-md">
					<ul class="divide-y divide-gray-200">
						{#each cartItems as item (item.id)}
							<li class="px-6 py-6">
								<div class="flex items-start space-x-4">
									<!-- Book image placeholder -->
									<div class="flex-shrink-0 w-20 h-28 bg-gray-200 rounded-md flex items-center justify-center">
										<svg class="w-8 h-8 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
											<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.746 0 3.332.477 4.5 1.253v13C19.832 18.477 18.246 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"/>
										</svg>
									</div>

									<!-- Book info -->
									<div class="flex-1 min-w-0">
										<h3 class="text-lg font-medium text-gray-900">{item.title}</h3>
										<p class="text-sm text-gray-500 mt-1">Tác giả: {item.author}</p>
										<div class="mt-2 flex items-center justify-between">
											<p class="text-lg font-semibold text-blue-600">{formatPrice(item.price)}</p>
											
											<!-- Quantity controls -->
											<div class="flex items-center space-x-2">
												<button 
													on:click={() => updateQuantity(item.id, item.quantity - 1)}
													class="w-8 h-8 rounded-full border border-gray-300 flex items-center justify-center hover:bg-gray-50"
												>
													<svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
														<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 12H4"/>
													</svg>
												</button>
												<span class="w-8 text-center font-medium">{item.quantity}</span>
												<button 
													on:click={() => updateQuantity(item.id, item.quantity + 1)}
													class="w-8 h-8 rounded-full border border-gray-300 flex items-center justify-center hover:bg-gray-50"
												>
													<svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
														<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"/>
													</svg>
												</button>
											</div>
										</div>
									</div>

									<!-- Remove button -->
									<button 
										on:click={() => removeItem(item.id)}
										class="flex-shrink-0 p-2 text-red-500 hover:text-red-700 hover:bg-red-50 rounded-full transition-colors duration-200"
									>
										<svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
											<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/>
										</svg>
									</button>
								</div>
							</li>
						{/each}
					</ul>
				</div>
			</div>

			<!-- Order summary -->
			<div class="lg:col-span-4 mt-8 lg:mt-0">
				<div class="bg-white rounded-lg shadow-md p-6 sticky top-24">
					<h2 class="text-lg font-medium text-gray-900 mb-4">Tóm tắt đơn hàng</h2>
					
					<div class="space-y-3">
						<div class="flex justify-between text-sm">
							<span class="text-gray-600">Tạm tính ({cartItems.length} sản phẩm)</span>
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
						<button class="w-full bg-blue-600 text-white py-3 px-4 rounded-md hover:bg-blue-700 transition-colors duration-200 font-medium">
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
