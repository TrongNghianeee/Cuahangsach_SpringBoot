<!-- front-end/src/routes/admin/+layout.svelte -->
<script lang="ts">
	import { page } from '$app/stores';
	
	function isActive(path: string): boolean {
		return $page.url.pathname === path;
	}
	
	function getLinkClass(path: string): string {
		const baseClass = "inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium";
		if (isActive(path)) {
			return `${baseClass} border-blue-500 text-blue-600`;
		}
		return `${baseClass} border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300`;
	}
</script>

<div class="min-h-screen bg-gray-100">
	<!-- Header -->
	<header class="bg-white shadow-sm border-b">
		<div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
			<div class="flex justify-between h-16">
				<div class="flex items-center">
					<h1 class="text-xl font-semibold text-gray-900">Admin Dashboard</h1>
				</div>
				<nav class="flex space-x-8">
					<a href="/admin" class={getLinkClass("/admin")}>
						Tổng quan
					</a>
					<a href="/admin/accounts" class={getLinkClass("/admin/accounts")}>
						Quản lý tài khoản
					</a>
					<a href="/admin/products" class={getLinkClass("/admin/products")}>
						Quản lý sản phẩm
					</a>
					<a href="/admin/inventory" class={getLinkClass("/admin/inventory")}>
						Quản lý kho
					</a>
				</nav>
			</div>
		</div>
	</header>

	<!-- Main content -->
	<main class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
		<slot />
	</main>
</div>