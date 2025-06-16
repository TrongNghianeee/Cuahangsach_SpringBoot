<!-- front-end/src/routes/admin/accounts/+page.svelte -->
<script lang="ts">	import { onMount } from 'svelte';
	import { writable } from 'svelte/store';
	import { authenticatedFetch } from '$lib/auth';
	import type { User, UserFormData, ApiResponse } from '$lib/types';
	// Stores
	const users = writable<User[]>([]);
	const filteredUsers = writable<User[]>([]);
	const loading = writable<boolean>(false);
	const message = writable<string>('');
	const error = writable<string>('');
	const showModal = writable<boolean>(false);
	const editMode = writable<boolean>(false);

	// Filter states
	let searchTerm = '';
	let filterRole = '';
	let filterStatus = '';

	// Form data
	let formData: UserFormData = {
		username: '',
		password: '',
		email: '',
		fullName: '',
		phone: '',
		address: '',
		role: 'KH',
		status: 'Active'
	};

	let editUserId: number | null = null;

	// API Base URL
	const API_BASE = 'http://localhost:8080/api/admin/users';	// Functions
	async function fetchUsers(): Promise<void> {
		loading.set(true);
		try {
			const response = await authenticatedFetch(API_BASE);			const result: ApiResponse<User[]> = await response.json();
			if (result.success && result.data) {
				users.set(result.data);
				applyFilters(); // Apply filters after fetching data
			} else {
				error.set(result.message);
			}
		} catch (err) {
			error.set('Lỗi kết nối: ' + (err as Error).message);
		} finally {
			loading.set(false);
		}
	}
	// Filter function
	function applyFilters(): void {
		if (!$users || $users.length === 0) {
			filteredUsers.set([]);
			return;
		}

		let filtered = $users;

		// Search by name or email
		if (searchTerm.trim()) {
			const search = searchTerm.toLowerCase().trim();
			filtered = filtered.filter(user => 
				user.username.toLowerCase().includes(search) ||
				user.email.toLowerCase().includes(search) ||
				(user.fullName && user.fullName.toLowerCase().includes(search))
			);
		}

		// Filter by role
		if (filterRole) {
			filtered = filtered.filter(user => user.role === filterRole);
		}

		// Filter by status
		if (filterStatus) {
			filtered = filtered.filter(user => user.status === filterStatus);
		}

		filteredUsers.set(filtered);
	}

	// Clear all filters
	function clearFilters(): void {
		searchTerm = '';
		filterRole = '';
		filterStatus = '';
		applyFilters();
	}

	async function submitForm(): Promise<void> {
		if (!validateForm()) return;

		// Type guard for editUserId
		if ($editMode && editUserId === null) {
			error.set('Lỗi: ID người dùng không hợp lệ');
			return;
		}

		loading.set(true);
		error.set('');
		message.set('');
		try {
			const url = $editMode ? `${API_BASE}/${editUserId}` : API_BASE;
			const method = $editMode ? 'PUT' : 'POST';

			const response = await authenticatedFetch(url, {
				method,
				headers: {
					'Content-Type': 'application/json'
				},
				body: JSON.stringify(formData)
			});

			const result: ApiResponse<User> = await response.json();

			if (result.success) {
				message.set(result.message);
				resetForm();
				showModal.set(false);
				fetchUsers();
			} else {
				error.set(result.message);
			}
		} catch (err) {
			error.set('Lỗi kết nối: ' + (err as Error).message);
		} finally {
			loading.set(false);
		}
	}

	async function editUser(user: User): Promise<void> {
		formData = {
			username: user.username,
			password: '',
			email: user.email,
			fullName: user.fullName || '',
			phone: user.phone || '',
			address: user.address || '',
			role: user.role,
			status: user.status
		};
		editUserId = user.userId;
		editMode.set(true);
		showModal.set(true);
	}	async function toggleUserStatus(userId: number, status: string): Promise<void> {
		loading.set(true);
		try {
			const response = await authenticatedFetch(`${API_BASE}/${userId}/status`, {
				method: 'PUT',
				headers: {
					'Content-Type': 'application/json'
				},
				body: JSON.stringify({ status })
			});

			const result: ApiResponse = await response.json();

			if (result.success) {
				message.set(result.message);
				fetchUsers();
			} else {
				error.set(result.message);
			}
		} catch (err) {
			error.set('Lỗi kết nối: ' + (err as Error).message);
		} finally {
			loading.set(false);
		}
	}

	function validateForm(): boolean {
		if (!formData.username.trim()) {
			error.set('Tên người dùng không được để trống');
			return false;
		}
		if (!$editMode && !formData.password.trim()) {
			error.set('Mật khẩu không được để trống');
			return false;
		}
		if (!formData.email.trim()) {
			error.set('Email không được để trống');
			return false;
		}
		return true;
	}
	function resetForm(): void {
		formData = {
			username: '',
			password: '',
			email: '',
			fullName: '',
			phone: '',
			address: '',
			role: 'KH',
			status: 'Active'
		};
		editUserId = null;
		editMode.set(false);
	}

	function openAddModal(): void {
		resetForm();
		showModal.set(true);
	}

	function closeModal(): void {
		showModal.set(false);
		resetForm();
		error.set('');
	}
	// Clear messages after 3 seconds
	$: if ($message) {
		setTimeout(() => message.set(''), 3000);
	}
	$: if ($error) {
		setTimeout(() => error.set(''), 5000);
	}

	// Auto-apply filters when search term or filter criteria change
	$: if (searchTerm !== undefined || filterRole !== undefined || filterStatus !== undefined) {
		applyFilters();
	}
	onMount(() => {
		fetchUsers();
		// Initialize filtered users
		filteredUsers.set([]);
	});
</script>

<div class="p-6">
	<div class="mb-6 flex items-center justify-between">
		<h1 class="text-3xl font-bold text-gray-900">Quản lý tài khoản</h1>
		<button
			class="flex items-center gap-2 rounded-lg bg-blue-600 px-4 py-2 text-white hover:bg-blue-700"
			on:click={openAddModal}
		>
			<svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
				<path
					stroke-linecap="round"
					stroke-linejoin="round"
					stroke-width="2"
					d="M12 6v6m0 0v6m0-6h6m-6 0H6"
				/>
			</svg>
			Thêm người dùng
		</button>
	</div>

	<!-- Messages -->
	{#if $message}
		<div class="mb-4 rounded border border-green-400 bg-green-100 px-4 py-3 text-green-700">
			{$message}
		</div>
	{/if}

	{#if $error}
		<div class="mb-4 rounded border border-red-400 bg-red-100 px-4 py-3 text-red-700">
			{$error}
		</div>	{/if}

	<!-- Search and Filter Section -->
	<div class="mb-6 rounded-lg bg-white p-4 shadow-md">
		<div class="grid grid-cols-1 gap-4 md:grid-cols-4">
			<!-- Search Input -->
			<div class="md:col-span-2">
				<label for="search" class="mb-1 block text-sm font-medium text-gray-700">
					Tìm kiếm theo tên, email
				</label>
				<div class="relative">
					<input
						id="search"
						type="text"
						bind:value={searchTerm}
						placeholder="Nhập tên người dùng, email hoặc họ tên..."
						class="w-full rounded-md border border-gray-300 pl-10 pr-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
					/>
					<div class="absolute inset-y-0 left-0 flex items-center pl-3">
						<svg class="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
							<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
						</svg>
					</div>
				</div>
			</div>

			<!-- Role Filter -->
			<div>
				<label for="roleFilter" class="mb-1 block text-sm font-medium text-gray-700">
					Lọc theo vai trò
				</label>
				<select
					id="roleFilter"
					bind:value={filterRole}
					class="w-full rounded-md border border-gray-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
				>
					<option value="">Tất cả vai trò</option>
					<option value="KH">Khách hàng</option>
					<option value="Nvien">Nhân viên</option>
					<option value="Qly">Quản lý</option>
				</select>
			</div>

			<!-- Status Filter -->
			<div>
				<label for="statusFilter" class="mb-1 block text-sm font-medium text-gray-700">
					Lọc theo trạng thái
				</label>
				<select
					id="statusFilter"
					bind:value={filterStatus}
					class="w-full rounded-md border border-gray-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
				>
					<option value="">Tất cả trạng thái</option>
					<option value="Active">Hoạt động</option>
					<option value="Lock">Bị khóa</option>
				</select>
			</div>
		</div>

		<!-- Filter Actions -->
		<div class="mt-4 flex items-center justify-between">
			<div class="flex items-center gap-4">
				<span class="text-sm text-gray-600">
					Hiển thị <span class="font-medium">{$filteredUsers.length}</span> / <span class="font-medium">{$users.length}</span> người dùng
				</span>
			</div>
			
			{#if searchTerm || filterRole || filterStatus}
				<button
					on:click={clearFilters}
					class="flex items-center gap-2 rounded-md border border-gray-300 px-3 py-1 text-sm text-gray-700 hover:bg-gray-50"
				>
					<svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
						<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
					</svg>
					Xóa bộ lọc
				</button>
			{/if}
		</div>
	</div>

	<!-- Loading indicator -->
	{#if $loading}
		<div class="flex justify-center py-8">
			<div class="h-12 w-12 animate-spin rounded-full border-b-2 border-blue-600"></div>
		</div>
	{/if}

	<!-- Users table -->
	<div class="overflow-hidden rounded-lg bg-white shadow-md">
		<table class="min-w-full divide-y divide-gray-200">
			<thead class="bg-gray-50">
				<tr>
					<th
						class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500"
					>
						Tên người dùng
					</th>
					<th
						class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500"
					>
						Email
					</th>
					<th
						class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500"
					>
						Họ tên
					</th>
					<th
						class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500"
					>
						Vai trò
					</th>
					<th
						class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500"
					>
						Trạng thái
					</th>
					<th
						class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500"
					>
						Thao tác
					</th>
				</tr>
			</thead>			<tbody class="divide-y divide-gray-200 bg-white">
				{#each $filteredUsers as user}
					<tr>
						<td class="whitespace-nowrap px-6 py-4 text-sm font-medium text-gray-900">
							{user.username}
						</td>
						<td class="whitespace-nowrap px-6 py-4 text-sm text-gray-500">
							{user.email}
						</td>
						<td class="whitespace-nowrap px-6 py-4 text-sm text-gray-500">
							{user.fullName || '-'}
						</td>
						<td class="whitespace-nowrap px-6 py-4">
							<span
								class="inline-flex rounded-full px-2 py-1 text-xs font-semibold
								{user.role === 'Qly'
									? 'bg-red-100 text-red-800'
									: user.role === 'Nvien'
										? 'bg-yellow-100 text-yellow-800'
										: 'bg-green-100 text-green-800'}"
							>
								{user.role === 'Qly'
									? 'Quản lý'
									: user.role === 'Nvien'
										? 'Nhân viên'
										: 'Khách hàng'}
							</span>
						</td>
						<td class="whitespace-nowrap px-6 py-4">
							<span
								class="inline-flex rounded-full px-2 py-1 text-xs font-semibold
								{user.status === 'Active' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'}"
							>
								{user.status === 'Active' ? 'Hoạt động' : 'Bị khóa'}
							</span>
						</td>
						<td class="whitespace-nowrap px-6 py-4 text-sm font-medium">
							<div class="flex space-x-3">
								<!-- Edit Icon -->
								<button
									class="rounded-md p-1 text-indigo-600 transition-colors hover:bg-indigo-50 hover:text-indigo-900"
									on:click={() => editUser(user)}
									title="Sửa thông tin"
								>
									<svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
										<path
											stroke-linecap="round"
											stroke-linejoin="round"
											stroke-width="2"
											d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"
										/>
									</svg>
								</button>

								<!-- Lock/Unlock Icon -->
								{#if user.status === 'Active'}
									<button
										class="rounded-md p-1 text-red-600 transition-colors hover:bg-red-50 hover:text-red-900"
										on:click={() => toggleUserStatus(user.userId, 'Lock')}
										title="Khóa tài khoản"
									>
										<!-- Unlocked/Open Lock Icon -->
										<svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
											<path
												stroke-linecap="round"
												stroke-linejoin="round"
												stroke-width="2"
												d="M8 11V7a4 4 0 118 0m-4 8v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2z"
											/>
										</svg>
									</button>
								{:else}
									<button
										class="rounded-md p-1 text-green-600 transition-colors hover:bg-green-50 hover:text-green-900"
										on:click={() => toggleUserStatus(user.userId, 'Active')}
										title="Mở khóa tài khoản"
									>
										<!-- Locked/Closed Lock Icon -->
										<svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
											<path
												stroke-linecap="round"
												stroke-linejoin="round"
												stroke-width="2"
												d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"
											/>
										</svg>
									</button>
								{/if}
							</div>
						</td>
					</tr>
				{/each}
			</tbody>
		</table>
		{#if $filteredUsers.length === 0 && !$loading}
			<div class="py-8 text-center text-gray-500">
				{#if $users.length === 0}
					Không có người dùng nào
				{:else if searchTerm || filterRole || filterStatus}
					Không tìm thấy người dùng nào phù hợp với bộ lọc
				{:else}
					Không có dữ liệu
				{/if}
			</div>
		{/if}
	</div>
</div>

<!-- Modal -->
{#if $showModal}
	<div class="fixed inset-0 z-50 h-full w-full overflow-y-auto bg-gray-600 bg-opacity-50">
		<div class="relative top-20 mx-auto w-full max-w-md rounded-lg border bg-white p-5 shadow-lg">
			<div class="mb-4 flex items-center justify-between">
				<h3 class="text-lg font-bold text-gray-900">
					{$editMode ? 'Sửa người dùng' : 'Thêm người dùng mới'}
				</h3>
				<button class="text-gray-400 hover:text-gray-600" on:click={closeModal}>
					<svg class="h-6 w-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
						<path
							stroke-linecap="round"
							stroke-linejoin="round"
							stroke-width="2"
							d="M6 18L18 6M6 6l12 12"
						/>
					</svg>
				</button>
			</div>
			<form on:submit|preventDefault={submitForm} class="space-y-4">
				<!-- Messages -->
				{#if $message}
					<div class="mb-4 rounded border border-green-400 bg-green-100 px-4 py-3 text-green-700">
						{$message}
					</div>
				{/if}

				{#if $error}
					<div class="mb-4 rounded border border-red-400 bg-red-100 px-4 py-3 text-red-700">
						{$error}
					</div>
				{/if}

				<div>
					<label for="username" class="mb-1 block text-sm font-medium text-gray-700">
						Tên người dùng *
					</label>
					<input
						id="username"
						type="text"
						bind:value={formData.username}
						class="w-full rounded-md border border-gray-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
						required
					/>
				</div>

				<div>
					<label for="password" class="mb-1 block text-sm font-medium text-gray-700">
						{$editMode ? 'Mật khẩu mới (để trống nếu không đổi)' : 'Mật khẩu *'}
					</label>
					<input
						id="password"
						type="password"
						bind:value={formData.password}
						class="w-full rounded-md border border-gray-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
						required={!$editMode}
					/>
				</div>

				<div>
					<label for="email" class="mb-1 block text-sm font-medium text-gray-700"> Email * </label>
					<input
						id="email"
						type="email"
						bind:value={formData.email}
						class="w-full rounded-md border border-gray-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
						required
					/>
				</div>

				<div>
					<label for="fullName" class="mb-1 block text-sm font-medium text-gray-700">
						Họ tên
					</label>
					<input
						id="fullName"
						type="text"
						bind:value={formData.fullName}
						class="w-full rounded-md border border-gray-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
					/>
				</div>

				<div>
					<label for="phone" class="mb-1 block text-sm font-medium text-gray-700">
						Số điện thoại
					</label>
					<input
						id="phone"
						type="tel"
						bind:value={formData.phone}
						class="w-full rounded-md border border-gray-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
					/>
				</div>

				<div>
					<label for="address" class="mb-1 block text-sm font-medium text-gray-700">
						Địa chỉ
					</label>
					<textarea
						id="address"
						bind:value={formData.address}
						rows="3"
						class="w-full rounded-md border border-gray-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
					></textarea>
				</div>

				<div>
					<label for="role" class="mb-1 block text-sm font-medium text-gray-700"> Vai trò </label>
					<select
						id="role"
						bind:value={formData.role}
						class="w-full rounded-md border border-gray-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
					>
						<option value="KH">Khách hàng</option>
						<option value="Nvien">Nhân viên</option>
						<option value="Qly">Quản lý</option>
					</select>
				</div>

				<div>
					<label for="status" class="mb-1 block text-sm font-medium text-gray-700">
						Trạng thái
					</label>
					<select
						id="status"
						bind:value={formData.status}
						class="w-full rounded-md border border-gray-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
					>
						<option value="Active">Hoạt động</option>
						<option value="Lock">Bị khóa</option>
					</select>
				</div>

				<div class="flex justify-end space-x-3 pt-4">
					<button
						type="button"
						on:click={closeModal}
						class="rounded-md border border-gray-300 px-4 py-2 text-gray-700 hover:bg-gray-50"
					>
						Hủy
					</button>
					<button
						type="submit"
						disabled={$loading}
						class="rounded-md bg-blue-600 px-4 py-2 text-white hover:bg-blue-700 disabled:opacity-50"
					>
						{$loading ? 'Đang xử lý...' : $editMode ? 'Cập nhật' : 'Thêm'}
					</button>
				</div>
			</form>
		</div>
	</div>
{/if}
