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

	// API URL
	const INVENTORY_API = 'http://localhost:8080/api/admin/inventory';

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
			<button 
				on:click={fetchInventoryTransactions}
				class="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-500"
				disabled={$loading}
			>
				{$loading ? 'Đang tải...' : 'Tải lại'}
			</button>
		</div>
	</div>
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
		</div>
	{:else if $inventoryTransactions.length === 0}
		<div class="text-center py-8">
			<svg class="mx-auto h-12 w-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
				<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" 
					d="M9 5H7a2 2 0 00-2 2v10a2 2 0 002 2h8a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4"/>
			</svg>
			<h3 class="mt-2 text-sm font-medium text-gray-900">Không có dữ liệu</h3>
			<p class="mt-1 text-sm text-gray-500">Chưa có giao dịch nhập xuất kho nào.</p>
		</div>
	{:else}
		<div class="bg-white shadow overflow-hidden sm:rounded-md">
			<div class="px-4 py-5 sm:p-6">
				<div class="mb-4">
					<h3 class="text-lg leading-6 font-medium text-gray-900">
						Tổng số giao dịch: {$inventoryTransactions.length}
					</h3>
				</div>
				
				<div class="overflow-x-auto">
					<table class="min-w-full divide-y divide-gray-200">
						<thead class="bg-gray-50">
							<tr>
								<th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
									Loại giao dịch
								</th>
								<th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
									Mã sách
								</th>
								<th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
									Số lượng
								</th>
								<th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
									Đơn giá
								</th>
								<th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
									Tổng giá trị
								</th>
								<th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
									Người thực hiện
								</th>
							</tr>
						</thead>
						<tbody class="bg-white divide-y divide-gray-200">
							{#each $inventoryTransactions as transaction, index}
								<tr class="hover:bg-gray-50">
									<td class="px-6 py-4 whitespace-nowrap">
										<span class="inline-flex px-2 py-1 text-xs font-semibold rounded-full {getTransactionTypeColor(transaction.transactionType)}">
											{transaction.transactionType}
										</span>
									</td>
									<td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
										{transaction.bookId}
									</td>
									<td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
										{transaction.quantity}
									</td>
									<td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
										{formatPrice(transaction.price)}
									</td>
									<td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
										{formatPrice(transaction.price * transaction.quantity)}
									</td>
									<td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
										User ID: {transaction.userId}
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
