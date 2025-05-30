<!-- front-end/src/routes/user/Order/+page.svelte -->
<script lang="ts">
	// Mock order data for demonstration
	let orders = [
		{
			id: 1,
			orderNumber: "DH001",
			date: "2024-03-15",
			status: "delivered",
			total: 430000,
			items: [
				{ title: "Sách Lập trình Web", quantity: 2, price: 250000 },
				{ title: "Cơ sở dữ liệu nâng cao", quantity: 1, price: 180000 }
			]
		},
		{
			id: 2,
			orderNumber: "DH002",
			date: "2024-03-18",
			status: "shipping",
			total: 320000,
			items: [
				{ title: "Thuật toán và cấu trúc dữ liệu", quantity: 1, price: 320000 }
			]
		},
		{
			id: 3,
			orderNumber: "DH003",
			date: "2024-03-20",
			status: "processing",
			total: 150000,
			items: [
				{ title: "Học Machine Learning cơ bản", quantity: 1, price: 150000 }
			]
		}
	];

	function getStatusColor(status: string): string {
		switch (status) {
			case 'delivered':
				return 'bg-green-100 text-green-800';
			case 'shipping':
				return 'bg-blue-100 text-blue-800';
			case 'processing':
				return 'bg-yellow-100 text-yellow-800';
			case 'cancelled':
				return 'bg-red-100 text-red-800';
			default:
				return 'bg-gray-100 text-gray-800';
		}
	}

	function getStatusText(status: string): string {
		switch (status) {
			case 'delivered':
				return 'Đã giao hàng';
			case 'shipping':
				return 'Đang giao hàng';
			case 'processing':
				return 'Đang xử lý';
			case 'cancelled':
				return 'Đã hủy';
			default:
				return 'Không xác định';
		}
	}

	function formatPrice(price: number): string {
		return new Intl.NumberFormat('vi-VN', {
			style: 'currency',
			currency: 'VND'
		}).format(price);
	}

	function formatDate(dateString: string): string {
		return new Date(dateString).toLocaleDateString('vi-VN');
	}

	let selectedOrder: any = null;

	function viewOrderDetails(order: any) {
		selectedOrder = order;
	}

	function closeOrderDetails() {
		selectedOrder = null;
	}
</script>

<svelte:head>
	<title>Đơn hàng - BookStore</title>
</svelte:head>

<div class="max-w-7xl mx-auto py-8 px-4 sm:px-6 lg:px-8">
	<!-- Page Header -->
	<div class="mb-8">
		<h1 class="text-3xl font-bold text-gray-900">Đơn hàng của bạn</h1>
		<p class="mt-2 text-gray-600">Theo dõi trạng thái và lịch sử đơn hàng</p>
	</div>

	{#if orders.length === 0}
		<!-- Empty orders -->
		<div class="text-center py-12">
			<svg class="mx-auto h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
				<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v10a2 2 0 002 2h8a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4"/>
			</svg>
			<h3 class="mt-2 text-sm font-medium text-gray-900">Chưa có đơn hàng</h3>
			<p class="mt-1 text-sm text-gray-500">Bạn chưa có đơn hàng nào. Hãy bắt đầu mua sắm!</p>
			<div class="mt-6">
				<a href="/user" class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700">
					Bắt đầu mua sắm
				</a>
			</div>
		</div>
	{:else}
		<!-- Orders list -->
		<div class="bg-white shadow overflow-hidden sm:rounded-md">
			<ul class="divide-y divide-gray-200">
				{#each orders as order}
					<li class="px-6 py-6">
						<div class="flex items-center justify-between">
							<div class="flex-1">
								<div class="flex items-center justify-between">
									<div>
										<h3 class="text-lg font-medium text-gray-900">
											Đơn hàng #{order.orderNumber}
										</h3>
										<p class="text-sm text-gray-500 mt-1">
											Đặt ngày: {formatDate(order.date)}
										</p>
									</div>
									<span class="inline-flex items-center px-3 py-1 rounded-full text-xs font-medium {getStatusColor(order.status)}">
										{getStatusText(order.status)}
									</span>
								</div>
								
								<div class="mt-3">
									<p class="text-sm text-gray-600">
										{order.items.length} sản phẩm • Tổng: {formatPrice(order.total)}
									</p>
									<div class="mt-2 text-sm text-gray-500">
										{#each order.items as item, index}
											{item.title}{index < order.items.length - 1 ? ', ' : ''}
										{/each}
									</div>
								</div>
							</div>

							<div class="ml-6 flex flex-col space-y-2">
								<button 
									on:click={() => viewOrderDetails(order)}
									class="inline-flex items-center px-3 py-2 border border-blue-600 rounded-md text-sm font-medium text-blue-600 hover:bg-blue-50 transition-colors duration-200"
								>
									<svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
										<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/>
										<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"/>
									</svg>
									Xem chi tiết
								</button>
								{#if order.status === 'delivered'}
									<button class="inline-flex items-center px-3 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 hover:bg-gray-50 transition-colors duration-200">
										<svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
											<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 16.5v2.25A2.25 2.25 0 005.25 21h13.5A2.25 2.25 0 0021 18.75V16.5M16.5 12L12 16.5m0 0L7.5 12m4.5 4.5V3"/>
										</svg>
										Mua lại
									</button>
								{/if}
							</div>
						</div>
					</li>
				{/each}
			</ul>
		</div>
	{/if}
</div>

<!-- Order Details Modal -->
{#if selectedOrder}
	<div class="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50" on:click={closeOrderDetails}>
		<div class="relative top-20 mx-auto p-5 border w-11/12 max-w-2xl shadow-lg rounded-md bg-white" on:click|stopPropagation>
			<!-- Modal header -->
			<div class="flex items-center justify-between pb-4 border-b">
				<h3 class="text-lg font-medium text-gray-900">
					Chi tiết đơn hàng #{selectedOrder.orderNumber}
				</h3>
				<button 
					on:click={closeOrderDetails}
					class="text-gray-400 hover:text-gray-600"
				>
					<svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
						<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
					</svg>
				</button>
			</div>

			<!-- Modal content -->
			<div class="mt-4">
				<div class="grid grid-cols-2 gap-4 mb-6">
					<div>
						<p class="text-sm font-medium text-gray-500">Ngày đặt hàng</p>
						<p class="text-sm text-gray-900">{formatDate(selectedOrder.date)}</p>
					</div>
					<div>
						<p class="text-sm font-medium text-gray-500">Trạng thái</p>
						<span class="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium {getStatusColor(selectedOrder.status)}">
							{getStatusText(selectedOrder.status)}
						</span>
					</div>
				</div>

				<div class="mb-6">
					<h4 class="text-sm font-medium text-gray-900 mb-3">Sản phẩm đã đặt</h4>
					<div class="border rounded-lg divide-y">
						{#each selectedOrder.items as item}
							<div class="p-4 flex justify-between items-center">
								<div>
									<p class="text-sm font-medium text-gray-900">{item.title}</p>
									<p class="text-sm text-gray-500">Số lượng: {item.quantity}</p>
								</div>
								<p class="text-sm font-medium text-gray-900">{formatPrice(item.price * item.quantity)}</p>
							</div>
						{/each}
					</div>
				</div>

				<div class="border-t pt-4">
					<div class="flex justify-between text-base font-medium text-gray-900">
						<span>Tổng cộng</span>
						<span>{formatPrice(selectedOrder.total)}</span>
					</div>
				</div>
			</div>

			<!-- Modal footer -->
			<div class="mt-6 flex justify-end">
				<button 
					on:click={closeOrderDetails}
					class="px-4 py-2 bg-gray-300 text-gray-700 rounded-md hover:bg-gray-400 transition-colors duration-200"
				>
					Đóng
				</button>
			</div>
		</div>
	</div>
{/if}
