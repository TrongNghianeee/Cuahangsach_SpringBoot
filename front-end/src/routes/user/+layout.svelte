<!-- front-end/src/routes/user/+layout.svelte -->
<script lang="ts">
	import { logout } from '$lib/auth';
	import { page } from '$app/stores';

	async function handleLogout() {
		await logout();
	}

	// Navigation items
	const navigation = [
		{ href: '/user', label: 'Trang chủ', icon: 'home' },
		{ href: '/user/Cart', label: 'Giỏ hàng', icon: 'cart' },
		{ href: '/user/Order', label: 'Đơn hàng', icon: 'order' },
		{ href: '/user/Profile', label: 'Hồ sơ', icon: 'profile' }
	];

	function getIconSvg(iconType: string): string {
		switch (iconType) {
			case 'home':
				return 'M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6';
			case 'cart':
				return 'M3 3h2l.4 2M7 13h10l4-8H5.4m1.6 8L5 3H3m4 10a1 1 0 100 2 1 1 0 000-2zm10 0a1 1 0 100 2 1 1 0 000-2z';
			case 'order':
				return 'M9 5H7a2 2 0 00-2 2v10a2 2 0 002 2h8a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-3 7h3m-3 4h3m-6-4h.01M9 16h.01';
			case 'profile':
				return 'M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z';
			default:
				return '';
		}
	}

	// Check if current route is active
	function isActive(href: string): boolean {
		return $page.url.pathname === href;
	}
</script>

<div class="min-h-screen bg-gray-50">
	<!-- Header -->
	<header class="bg-white shadow-sm border-b sticky top-0 z-50">
		<div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
			<div class="flex justify-between h-16">
				<!-- Logo -->
				<div class="flex items-center">
					<a href="/user" class="flex items-center space-x-2">
						<div class="w-8 h-8 bg-blue-600 rounded-lg flex items-center justify-center">
							<svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
								<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.746 0 3.332.477 4.5 1.253v13C19.832 18.477 18.246 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"/>
							</svg>
						</div>
						<h1 class="text-xl font-bold text-gray-900">BookStore</h1>
					</a>
				</div>

				<!-- Navigation -->
				<nav class="hidden md:flex items-center space-x-8">
					{#each navigation as item}
						<a 
							href={item.href} 
							class="flex items-center space-x-2 px-3 py-2 rounded-md text-sm font-medium transition-colors duration-200 {
								isActive(item.href) 
									? 'text-blue-600 bg-blue-50' 
									: 'text-gray-700 hover:text-blue-600 hover:bg-gray-50'
							}"
						>
							<svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
								<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d={getIconSvg(item.icon)}/>
							</svg>
							<span>{item.label}</span>
						</a>
					{/each}
				</nav>

				<!-- User actions -->
				<div class="flex items-center space-x-4">
					<!-- Mobile menu button -->
					<button class="md:hidden p-2 rounded-md text-gray-400 hover:text-gray-500 hover:bg-gray-100">
						<svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
							<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16"/>
						</svg>
					</button>

					<!-- Logout button -->
					<button 
						on:click={handleLogout} 
						class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-red-600 hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500 transition-colors duration-200"
					>
						<svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
							<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"/>
						</svg>
						Đăng xuất
					</button>
				</div>
			</div>
		</div>

		<!-- Mobile navigation -->
		<div class="md:hidden border-t border-gray-200">
			<div class="px-2 pt-2 pb-3 space-y-1">
				{#each navigation as item}
					<a 
						href={item.href}
						class="flex items-center space-x-3 px-3 py-2 rounded-md text-base font-medium {
							isActive(item.href) 
								? 'text-blue-600 bg-blue-50' 
								: 'text-gray-700 hover:text-blue-600 hover:bg-gray-50'
						}"
					>
						<svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
							<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d={getIconSvg(item.icon)}/>
						</svg>
						<span>{item.label}</span>
					</a>
				{/each}
			</div>
		</div>
	</header>

	<!-- Main content -->
	<main class="flex-1">
		<slot />
	</main>
</div>
