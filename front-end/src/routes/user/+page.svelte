<!-- front-end/src/routes/user/+page.svelte -->
<script lang="ts">
	import { logout } from '$lib/auth';

	async function handleLogout() {
		await logout();
	}
</script>

<div class="min-h-screen bg-gray-50">
	<!-- Header -->
	<header class="bg-white shadow-sm border-b">
		<div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
			<div class="flex justify-between h-16">
				<div class="flex items-center">
					<h1 class="text-xl font-semibold text-gray-900">Bookstore</h1>
				</div>
				<div class="flex items-center space-x-4">
					<nav class="flex space-x-6">
						<a href="/user" class="text-gray-700 hover:text-blue-600 font-medium">
							Trang chủ
						</a>
						<a href="/user/orders" class="text-gray-700 hover:text-blue-600 font-medium">
							Đơn hàng
						</a>
						<a href="/user/profile" class="text-gray-700 hover:text-blue-600 font-medium">
							Hồ sơ
						</a>
					</nav>
					<button 
						on:click={handleLogout} 
						class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-red-600 hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500 transition-colors duration-200"
					>
						<svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
							<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"></path>
						</svg>
						Đăng xuất
					</button>
				</div>
			</div>
		</div>
	</header>

	<!-- Main content -->
	<main class="max-w-7xl mx-auto py-12 px-4 sm:px-6 lg:px-8">
		<div class="text-center">
			<h1 class="text-4xl font-bold text-gray-900 mb-4">Chào mừng đến với cửa hàng bán sách</h1>
			<p class="text-lg text-gray-600">Khám phá bộ sưu tập sách phong phú của chúng tôi</p>
		</div>
		
		<!-- Add more content here for the bookstore homepage -->
		<div class="mt-12 grid grid-cols-1 md:grid-cols-3 gap-8">
			<div class="bg-white rounded-lg shadow-md p-6 text-center">
				<div class="w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-4">
					<svg class="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
						<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.746 0 3.332.477 4.5 1.253v13C19.832 18.477 18.246 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"/>
					</svg>
				</div>
				<h3 class="text-lg font-semibold text-gray-900 mb-2">Sách mới</h3>
				<p class="text-gray-600">Khám phá những cuốn sách mới nhất</p>
			</div>
			
			<div class="bg-white rounded-lg shadow-md p-6 text-center">
				<div class="w-12 h-12 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
					<svg class="w-6 h-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
						<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"/>
					</svg>
				</div>
				<h3 class="text-lg font-semibold text-gray-900 mb-2">Yêu thích</h3>
				<p class="text-gray-600">Những cuốn sách được yêu thích nhất</p>
			</div>
			
			<div class="bg-white rounded-lg shadow-md p-6 text-center">
				<div class="w-12 h-12 bg-purple-100 rounded-full flex items-center justify-center mx-auto mb-4">
					<svg class="w-6 h-6 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
						<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.99 1.99 0 013 12V7a4 4 0 014-4z"/>
					</svg>
				</div>
				<h3 class="text-lg font-semibold text-gray-900 mb-2">Khuyến mãi</h3>
				<p class="text-gray-600">Ưu đãi đặc biệt cho bạn</p>
			</div>
		</div>
	</main>
</div>