<!-- front-end/src/routes/admin/inventory/+page.svelte -->
<script lang="ts">
	import { onMount } from 'svelte';
	import { writable } from 'svelte/store';
	import { authenticatedFetch } from '$lib/auth';
	import type { InventoryDTO, ApiResponse } from '$lib/types';
	// Stores
	const inventoryTransactions = writable<InventoryDTO[]>([]);
	const loading = writable<boolean>(false);
	const error = writable<string>('');

	// Filter states
	const searchTerm = writable<string>('');
	const selectedTransactionType = writable<string>('');
	const fromDate = writable<string>('');
	const toDate = writable<string>('');
	const showFilters = writable<boolean>(false);
	// API URL
	const INVENTORY_API = 'http://localhost:8080/api/admin/inventory';

	// Derived store for filtered transactions
	import { derived } from 'svelte/store';
	
	const filteredTransactions = derived(
		[inventoryTransactions, searchTerm, selectedTransactionType, fromDate, toDate],
		([$inventoryTransactions, $searchTerm, $selectedTransactionType, $fromDate, $toDate]) => {
			let filtered = [...$inventoryTransactions];

			// Filter by search term (bookTitle or username)
			if ($searchTerm.trim()) {
				const term = $searchTerm.toLowerCase().trim();
				filtered = filtered.filter(transaction => 
					(transaction.bookTitle && transaction.bookTitle.toLowerCase().includes(term)) ||
					(transaction.username && transaction.username.toLowerCase().includes(term))
				);
			}

			// Filter by transaction type
			if ($selectedTransactionType) {
				filtered = filtered.filter(transaction => 
					transaction.transactionType === $selectedTransactionType
				);
			}

			// Filter by date range
			if ($fromDate) {
				const fromDateTime = new Date($fromDate);
				filtered = filtered.filter(transaction => {
					if (!transaction.transactionDate) return false;
					const transactionDateTime = new Date(transaction.transactionDate);
					return transactionDateTime >= fromDateTime;
				});
			}

			if ($toDate) {
				const toDateTime = new Date($toDate);
				toDateTime.setHours(23, 59, 59, 999); // End of day
				filtered = filtered.filter(transaction => {
					if (!transaction.transactionDate) return false;
					const transactionDateTime = new Date(transaction.transactionDate);
					return transactionDateTime <= toDateTime;
				});
			}

			// Sort by transaction date (newest first)
			filtered.sort((a, b) => {
				if (!a.transactionDate) return 1;
				if (!b.transactionDate) return -1;
				return new Date(b.transactionDate).getTime() - new Date(a.transactionDate).getTime();
			});

			return filtered;
		}
	);

	// Functions
	async function fetchInventoryTransactions(): Promise<void> {
		loading.set(true);
		try {
			const response = await authenticatedFetch(INVENTORY_API);
			console.log('API Response status:', response.status, response.statusText);
			if (response.ok) {
			const result = await response.json();
			console.log('Raw API Result:', result);
			console.log('Type of result:', typeof result);
			console.log('Is array?', Array.isArray(result));
			
			// The inventory API returns the data directly as an array
			if (Array.isArray(result)) {
				console.log('Direct array response, length:', result.length);
				inventoryTransactions.set(result);
				error.set(''); // Clear any previous errors
			} else {
				console.log('Unexpected response format:', result);
				error.set('Định dạng dữ liệu không hợp lệ');
			}
		}else {
				console.log('Response not ok:', response.status, response.statusText);
				error.set('Lỗi kết nối đến server');
			}
		} catch (err) {
			console.error('Fetch error:', err);
			error.set('Lỗi kết nối: ' + (err as Error).message);
		} finally {
			loading.set(false);
		}
	}

	function formatDate(dateString: string): string {
		return new Date(dateString).toLocaleString('vi-VN');
	}

	function formatPrice(price: number): string {
		return new Intl.NumberFormat('vi-VN', {
			style: 'currency',
			currency: 'VND'
		}).format(price);
	}
	function getTransactionTypeColor(type: string): string {
		return type === 'Nhập' ? 'text-green-600 bg-green-100' : 'text-red-600 bg-red-100';
	}

	// Filter functions
	function clearAllFilters(): void {
		searchTerm.set('');
		selectedTransactionType.set('');
		fromDate.set('');
		toDate.set('');
	}

	function toggleFilters(): void {
		showFilters.update(show => !show);
	}

	function getFilterSummary(): string {
		let summary = [];
		if ($searchTerm) summary.push(`Tìm kiếm: "${$searchTerm}"`);
		if ($selectedTransactionType) summary.push(`Loại: ${$selectedTransactionType}`);
		if ($fromDate) summary.push(`Từ: ${new Date($fromDate).toLocaleDateString('vi-VN')}`);
		if ($toDate) summary.push(`Đến: ${new Date($toDate).toLocaleDateString('vi-VN')}`);
		
		return summary.length > 0 ? summary.join(' • ') : 'Không có bộ lọc';
	}

	// Clear error after 5 seconds
	$: if ($error) {
		setTimeout(() => error.set(''), 5000);
	}

	onMount(() => {
		fetchInventoryTransactions();
	});
</script>

<div class="p-6">	<!-- Page Header -->
	<div class="mb-6">
		<div class="flex justify-between items-center">
			<div>
				<h2 class="text-2xl font-bold text-gray-900 mb-2">Lịch sử Nhập Xuất Kho</h2>
				<p class="text-gray-600">Danh sách tất cả các giao dịch nhập và xuất kho</p>
			</div>
			<div class="flex items-center space-x-4">
				<span class="text-sm text-gray-500">
					{$filteredTransactions.length} / {$inventoryTransactions.length} giao dịch
				</span>
				<button
					on:click={toggleFilters}
					class="inline-flex items-center px-3 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
				>
					<svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
						<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z"/>
					</svg>
					{$showFilters ? 'Ẩn bộ lọc' : 'Hiện bộ lọc'}
				</button>
				<button 
					on:click={fetchInventoryTransactions}
					class="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-500"
					disabled={$loading}
				>
					{$loading ? 'Đang tải...' : 'Tải lại'}
				</button>
			</div>
		</div>
	</div>

	<!-- Filters Panel -->
	{#if $showFilters}
		<div class="mb-6 bg-white rounded-lg shadow p-4">
			<div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-4">
				<!-- Search -->
				<div>
					<label class="block text-sm font-medium text-gray-700 mb-1">Tìm kiếm</label>
					<input
						bind:value={$searchTerm}
						type="text"
						placeholder="Tên sách hoặc người thực hiện..."
						class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent text-sm"
					/>
				</div>

				<!-- Transaction Type Filter -->
				<div>
					<label class="block text-sm font-medium text-gray-700 mb-1">Loại giao dịch</label>
					<select
						bind:value={$selectedTransactionType}
						class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent text-sm"
					>
						<option value="">Tất cả</option>
						<option value="Nhập">Nhập kho</option>
						<option value="Xuất">Xuất kho</option>
					</select>
				</div>

				<!-- From Date -->
				<div>
					<label class="block text-sm font-medium text-gray-700 mb-1">Từ ngày</label>
					<input
						bind:value={$fromDate}
						type="date"
						class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent text-sm"
					/>
				</div>

				<!-- To Date -->
				<div>
					<label class="block text-sm font-medium text-gray-700 mb-1">Đến ngày</label>
					<input
						bind:value={$toDate}
						type="date"
						class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent text-sm"
					/>
				</div>
			</div>

			<!-- Filter Summary and Clear -->
			<div class="flex items-center justify-between pt-3 border-t border-gray-200">
				<div class="text-sm text-gray-600">
					<span class="font-medium">Bộ lọc hiện tại:</span>
					<span>{getFilterSummary()}</span>
				</div>
				<button
					on:click={clearAllFilters}
					class="inline-flex items-center px-3 py-1 text-xs font-medium text-gray-600 hover:text-gray-800 border border-gray-300 rounded-md hover:bg-gray-100"
					disabled={!$searchTerm && !$selectedTransactionType && !$fromDate && !$toDate}
				>
					<svg class="w-3 h-3 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
						<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
					</svg>
					Xóa bộ lọc
				</button>
			</div>
		</div>
	{/if}
	<!-- Alert Messages -->
	{#if $error}
		<div class="mb-4 p-4 bg-red-100 border border-red-400 text-red-700 rounded">
			{$error}
		</div>
	{/if}

	<!-- Debug Info (remove this in production) -->
	<!-- <div class="mb-4 p-4 bg-blue-100 border border-blue-400 text-blue-700 rounded">
		<h4 class="font-bold">Debug Info:</h4>
		<p>Loading: {$loading}</p>
		<p>Error: {$error}</p>
		<p>Transactions Count: {$inventoryTransactions.length}</p>
		<p>Transactions Data: {JSON.stringify($inventoryTransactions)}</p>
	</div> -->

	<!-- Transaction Table -->
	{#if $loading}
		<div class="flex justify-center items-center py-8">
			<div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
			<span class="ml-2 text-gray-600">Đang tải dữ liệu...</span>
		</div>	{:else if $inventoryTransactions.length === 0}
		<div class="text-center py-8">
			<svg class="mx-auto h-12 w-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
				<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" 
					d="M9 5H7a2 2 0 00-2 2v10a2 2 0 002 2h8a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4"/>
			</svg>
			<h3 class="mt-2 text-sm font-medium text-gray-900">Không có dữ liệu</h3>
			<p class="mt-1 text-sm text-gray-500">Chưa có giao dịch nhập xuất kho nào.</p>
		</div>
	{:else if $filteredTransactions.length === 0}
		<div class="text-center py-8">
			<svg class="mx-auto h-12 w-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
				<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/>
			</svg>
			<h3 class="mt-2 text-sm font-medium text-gray-900">Không tìm thấy giao dịch</h3>
			<p class="mt-1 text-sm text-gray-500">Không có giao dịch nào phù hợp với bộ lọc hiện tại.</p>
			<div class="mt-6">
				<button
					on:click={clearAllFilters}
					class="inline-flex items-center px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50"
				>
					Xóa bộ lọc
				</button>
			</div>
		</div>
	{:else}
		<div class="bg-white shadow overflow-hidden sm:rounded-md">
			<div class="px-4 py-5 sm:p-6">				<div class="mb-4">
					<h3 class="text-lg leading-6 font-medium text-gray-900">
						Hiển thị {$filteredTransactions.length} / {$inventoryTransactions.length} giao dịch
					</h3>
				</div>
				
				<div class="overflow-x-auto">
					<table class="min-w-full divide-y divide-gray-200">
						<thead class="bg-gray-50">
							<tr>								<th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
									Loại giao dịch
								</th>
								<th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
									Tên sách
								</th>
								<th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
									Số lượng
								</th>
								<th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
									Đơn giá
								</th>
								<th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
									Tổng giá trị
								</th>								<th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
									Người thực hiện
								</th>
								<th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
									Thời gian
								</th>
							</tr>
						</thead>
						<tbody class="bg-white divide-y divide-gray-200">
							{#each $filteredTransactions as transaction, index}
								<tr class="hover:bg-gray-50">									<td class="px-6 py-4 whitespace-nowrap">
										<span class="inline-flex px-2 py-1 text-xs font-semibold rounded-full {getTransactionTypeColor(transaction.transactionType)}">
											{transaction.transactionType}
										</span>
									</td>
									<td class="px-6 py-4 whitespace-nowrap">
										<div class="text-sm font-medium text-gray-900">
											{transaction.bookTitle || `Sách ID: ${transaction.bookId}`}
										</div>
										<div class="text-sm text-gray-500">
											ID: {transaction.bookId}
										</div>
									</td>
									<td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
										{transaction.quantity}
									</td>
									<td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
										{formatPrice(transaction.price)}
									</td>
									<td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
										{formatPrice(transaction.price * transaction.quantity)}
									</td>									<td class="px-6 py-4 whitespace-nowrap">
										<div class="text-sm font-medium text-gray-900">
											{transaction.username || `User ID: ${transaction.userId}`}
										</div>
										<div class="text-sm text-gray-500">
											ID: {transaction.userId}
										</div>
									</td>
									<td class="px-6 py-4 whitespace-nowrap">
										<div class="text-sm text-gray-900">
											{transaction.transactionDate ? formatDate(transaction.transactionDate) : 'N/A'}
										</div>
									</td>
								</tr>
							{/each}
						</tbody>
					</table>
				</div>
			</div>
		</div>
	{/if}
</div>
