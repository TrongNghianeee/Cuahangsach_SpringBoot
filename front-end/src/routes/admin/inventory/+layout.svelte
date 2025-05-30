<!-- front-end/src/routes/admin/inventory/+layout.svelte -->
<script lang="ts">
	import { page } from '$app/stores';
	
	function isActive(path: string): boolean {
		return $page.url.pathname === path;
	}
	
	function getTabClass(path: string): string {
		const baseClass = "inline-flex items-center px-4 py-2 text-sm font-medium border-b-2 whitespace-nowrap";
		if (isActive(path)) {
			return `${baseClass} border-blue-500 text-blue-600 bg-blue-50`;
		}
		return `${baseClass} border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300`;
	}
</script>

<div class="min-h-screen bg-gray-50">
	<div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
		<!-- Header -->
		<div class="mb-8">
			<h1 class="text-3xl font-bold text-gray-900 mb-2">Theo dõi lịch sử nhập xuất kho và báo cáo thống kê</h1>
		</div>

		<!-- Navigation Tabs -->
		<div class="bg-white rounded-lg shadow-sm mb-6">
			<nav class="flex space-x-8 px-6" aria-label="Tabs">
				<a href="/admin/inventory" class={getTabClass("/admin/inventory")}>
					<svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
						<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" 
							d="M9 5H7a2 2 0 00-2 2v10a2 2 0 002 2h8a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-3 7h3m-3 4h3m-6-4h.01M9 16h.01"/>
					</svg>
					Lịch sử Nhập Xuất
				</a>
				
				<a href="/admin/inventory/reports" class={getTabClass("/admin/inventory/reports")}>
					<svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
						<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" 
							d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"/>
					</svg>
					Báo cáo & Thống kê
				</a>
			</nav>
		</div>

		<!-- Page Content -->
		<div class="bg-white rounded-lg shadow-sm">
			<slot />
		</div>
	</div>
</div>