<script lang="ts">
	// No imports needed since layout handles navigation
</script>

<svelte:head>
	<title>Trang chủ - BookStore</title>
</svelte:head>

<!-- Page content -->
<div class="max-w-7xl mx-auto py-12 px-4 sm:px-6 lg:px-8">

	<!-- Featured books section -->
	<div class="mt-16">
		<h2 class="text-2xl font-bold text-gray-900 mb-8">Sách nổi bật</h2>
		<div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
			{#each Array(4) as _, i}
				<div class="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition-shadow duration-200">
					<div class="h-48 bg-gray-200 flex items-center justify-center">
						<svg class="w-16 h-16 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
							<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.746 0 3.332.477 4.5 1.253v13C19.832 18.477 18.246 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"/>
						</svg>
					</div>
					<div class="p-4">
						<h3 class="font-semibold text-gray-900 mb-2">Tên sách {i + 1}</h3>
						<p class="text-gray-600 text-sm mb-2">Tác giả {i + 1}</p>
						<div class="flex justify-between items-center">
							<span class="text-lg font-bold text-blue-600">150,000₫</span>
							<button class="px-3 py-1 bg-blue-600 text-white text-sm rounded hover:bg-blue-700 transition-colors duration-200">
								Thêm vào giỏ
							</button>
						</div>
					</div>
				</div>
			{/each}
		</div>
	</div>
</div>