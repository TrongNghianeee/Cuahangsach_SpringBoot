<!-- front-end/src/routes/admin/+page.svelte -->
<script lang="ts">
	import { onMount } from 'svelte';

	// Sample data for dashboard
	let stats = {
		totalUsers: 0,
		totalProducts: 0,
		totalOrders: 0,
		revenue: 0
	};

	onMount(async () => {
		// TODO: Fetch real data from API
		// For now, using mock data
		stats = {
			totalUsers: 150,
			totalProducts: 240,
			totalOrders: 89,
			revenue: 25000000
		};
	});

	function formatCurrency(amount: number): string {
		return new Intl.NumberFormat('vi-VN', {
			style: 'currency',
			currency: 'VND'
		}).format(amount);
	}
</script>

<div class="p-6">
	<div class="mb-8">
		<h1 class="text-3xl font-bold text-gray-900">Tổng quan hệ thống</h1>
		<p class="text-gray-600 mt-2">Xem tổng quan về hoạt động của cửa hàng sách</p>
	</div>

	<!-- Stats Grid -->
	<div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
		<!-- Total Users -->
		<div class="bg-white rounded-lg shadow p-6">
			<div class="flex items-center">
				<div class="p-3 rounded-full bg-blue-100 text-blue-600">
					<svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
						<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197m13.5-9a2.5 2.5 0 11-5 0 2.5 2.5 0 015 0z" />
					</svg>
				</div>
				<div class="ml-4">
					<p class="text-sm font-medium text-gray-600">Tổng người dùng</p>
					<p class="text-2xl font-bold text-gray-900">{stats.totalUsers}</p>
				</div>
			</div>
		</div>

		<!-- Total Products -->
		<div class="bg-white rounded-lg shadow p-6">
			<div class="flex items-center">
				<div class="p-3 rounded-full bg-green-100 text-green-600">
					<svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
						<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.746 0 3.332.477 4.5 1.253v13C19.832 18.477 18.246 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
					</svg>
				</div>
				<div class="ml-4">
					<p class="text-sm font-medium text-gray-600">Tổng sản phẩm</p>
					<p class="text-2xl font-bold text-gray-900">{stats.totalProducts}</p>
				</div>
			</div>
		</div>

		<!-- Total Orders -->
		<div class="bg-white rounded-lg shadow p-6">
			<div class="flex items-center">
				<div class="p-3 rounded-full bg-yellow-100 text-yellow-600">
					<svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
						<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
					</svg>
				</div>
				<div class="ml-4">
					<p class="text-sm font-medium text-gray-600">Tổng đơn hàng</p>
					<p class="text-2xl font-bold text-gray-900">{stats.totalOrders}</p>
				</div>
			</div>
		</div>

		<!-- Revenue -->
		<div class="bg-white rounded-lg shadow p-6">
			<div class="flex items-center">
				<div class="p-3 rounded-full bg-purple-100 text-purple-600">
					<svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
						<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1" />
					</svg>
				</div>
				<div class="ml-4">
					<p class="text-sm font-medium text-gray-600">Doanh thu</p>
					<p class="text-2xl font-bold text-gray-900">{formatCurrency(stats.revenue)}</p>
				</div>
			</div>
		</div>
	</div>

	<!-- Quick Actions -->
	<div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
		<!-- Quick Links -->
		<div class="bg-white rounded-lg shadow p-6">
			<h2 class="text-lg font-semibold text-gray-900 mb-4">Quản lý nhanh</h2>
			<div class="space-y-3">
				<a href="/admin/accounts" class="flex items-center p-3 rounded-lg hover:bg-gray-50 transition-colors">
					<div class="p-2 rounded bg-blue-100 text-blue-600 mr-3">
						<svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
							<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197m13.5-9a2.5 2.5 0 11-5 0 2.5 2.5 0 015 0z" />
						</svg>
					</div>
					<span class="font-medium">Quản lý tài khoản</span>
				</a>

				<a href="/admin/products" class="flex items-center p-3 rounded-lg hover:bg-gray-50 transition-colors">
					<div class="p-2 rounded bg-green-100 text-green-600 mr-3">
						<svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
							<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.746 0 3.332.477 4.5 1.253v13C19.832 18.477 18.246 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
						</svg>
					</div>
					<span class="font-medium">Quản lý sản phẩm</span>
				</a>

				<a href="/admin/inventory" class="flex items-center p-3 rounded-lg hover:bg-gray-50 transition-colors">
					<div class="p-2 rounded bg-yellow-100 text-yellow-600 mr-3">
						<svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
							<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
						</svg>
					</div>
					<span class="font-medium">Báo cáo & Thống kê</span>
				</a>
			</div>
		</div>

		<!-- Recent Activity -->
		<div class="bg-white rounded-lg shadow p-6">
			<h2 class="text-lg font-semibold text-gray-900 mb-4">Hoạt động gần đây</h2>
			<div class="space-y-3">
				<div class="flex items-center p-3 bg-gray-50 rounded-lg">
					<div class="w-2 h-2 bg-green-500 rounded-full mr-3"></div>
					<div class="flex-1">
						<p class="text-sm font-medium">Người dùng mới đăng ký</p>
						<p class="text-xs text-gray-500">2 phút trước</p>
					</div>
				</div>

				<div class="flex items-center p-3 bg-gray-50 rounded-lg">
					<div class="w-2 h-2 bg-blue-500 rounded-full mr-3"></div>
					<div class="flex-1">
						<p class="text-sm font-medium">Đơn hàng mới được tạo</p>
						<p class="text-xs text-gray-500">5 phút trước</p>
					</div>
				</div>

				<div class="flex items-center p-3 bg-gray-50 rounded-lg">
					<div class="w-2 h-2 bg-yellow-500 rounded-full mr-3"></div>
					<div class="flex-1">
						<p class="text-sm font-medium">Sản phẩm sắp hết hàng</p>
						<p class="text-xs text-gray-500">10 phút trước</p>
					</div>
				</div>

				<div class="flex items-center p-3 bg-gray-50 rounded-lg">
					<div class="w-2 h-2 bg-purple-500 rounded-full mr-3"></div>
					<div class="flex-1">
						<p class="text-sm font-medium">Báo cáo tháng được tạo</p>
						<p class="text-xs text-gray-500">1 giờ trước</p>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
