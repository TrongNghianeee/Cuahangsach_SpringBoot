<!-- front-end/src/routes/user/Profile/+page.svelte -->
<script lang="ts">
	import { onMount } from 'svelte';
	import { getCurrentUser, authenticatedFetch } from '$lib/auth';

	interface User {
		userId: number;
		username: string;
		email: string;
		fullName: string;
		phone: string;
		address: string;
		role: string;
		status: string;
	}

	let user: User | null = null;
	let isEditing = false;
	let editForm: Partial<User & { password?: string; confirmPassword?: string }> = {};
	let loading = false;
	let error = '';
	let successMessage = '';

	// Load user data on component mount
	onMount(async () => {
		await loadUserData();
	});

	async function loadUserData() {
		try {
			loading = true;
			error = '';
			const currentUser = await getCurrentUser();
			if (currentUser) {
				user = currentUser;
				editForm = { ...currentUser, password: '', confirmPassword: '' };
			} else {
				error = 'Không thể tải thông tin người dùng';
			}
		} catch (err) {
			error = 'Lỗi khi tải thông tin người dùng: ' + (err as Error).message;
		} finally {
			loading = false;
		}
	}

	function startEdit() {
		isEditing = true;
		editForm = { ...user, password: '', confirmPassword: '' };
		error = '';
		successMessage = '';
	}

	function cancelEdit() {
		isEditing = false;
		editForm = { ...user, password: '', confirmPassword: '' };
		error = '';
		successMessage = '';
	}

	async function saveProfile() {
		if (!user) return;

		// Validate password if provided
		if (editForm.password || editForm.confirmPassword) {
			if (editForm.password !== editForm.confirmPassword) {
				error = 'Mật khẩu xác nhận không khớp';
				return;
			}
			if (editForm.password && editForm.password.length < 6) {
				error = 'Mật khẩu phải có ít nhất 6 ký tự';
				return;
			}
		}

		try {
			loading = true;
			error = '';
			successMessage = '';
			
			// Prepare update data
			const updateData: any = {
				username: editForm.username || '',
				email: editForm.email || '',
				fullName: editForm.fullName || '',
				phone: editForm.phone || '',
				address: editForm.address || ''
			};

			// Include password only if provided
			if (editForm.password && editForm.password.length > 0) {
				updateData.password = editForm.password;
			}

			const response = await authenticatedFetch('http://localhost:8080/api/user/profile', {
				method: 'PUT',
				body: JSON.stringify(updateData)
			});

			const result = await response.json();

			if (result.success) {
				// Update local user data
				user = {
					...user,
					username: updateData.username,
					email: updateData.email,
					fullName: updateData.fullName,
					phone: updateData.phone,
					address: updateData.address
				};
				editForm = { ...user, password: '', confirmPassword: '' };
				isEditing = false;
				successMessage = result.message || 'Cập nhật thông tin thành công!';
			} else {
				error = result.message || 'Lỗi khi cập nhật thông tin';
			}
		} catch (err) {
			error = 'Lỗi kết nối: ' + (err as Error).message;
		} finally {
			loading = false;
		}
	}

	function uploadAvatar(event: Event) {
		const input = event.target as HTMLInputElement;
		if (input.files && input.files[0]) {
			// Handle avatar upload
			alert('Chức năng tải ảnh đại diện sẽ được phát triển!');
		}
	}
</script>

<svelte:head>
	<title>Hồ sơ cá nhân - BookStore</title>
</svelte:head>

<div class="max-w-4xl mx-auto py-8 px-4 sm:px-6 lg:px-8">
	<!-- Page Header -->
	<div class="mb-8">
		<h1 class="text-3xl font-bold text-gray-900">Hồ sơ cá nhân</h1>
		<p class="mt-2 text-gray-600">Quản lý thông tin tài khoản và cài đặt</p>
	</div>

	<!-- Loading State -->
	{#if loading}
		<div class="flex justify-center items-center py-12">
			<div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
		</div>
	{:else if !user}
		<!-- Error State -->
		<div class="bg-red-50 border border-red-200 rounded-lg p-6">
			<div class="flex items-center">
				<svg class="w-5 h-5 text-red-400 mr-2" fill="currentColor" viewBox="0 0 20 20">
					<path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd"/>
				</svg>
				<span class="text-red-800">Không thể tải thông tin người dùng</span>
			</div>
			{#if error}
				<p class="mt-2 text-sm text-red-600">{error}</p>
			{/if}
			<button 
				on:click={loadUserData}
				class="mt-4 px-4 py-2 bg-red-600 text-white rounded-md text-sm font-medium hover:bg-red-700 transition-colors duration-200"
			>
				Thử lại
			</button>
		</div>
	{:else}
		<!-- Success/Error Messages -->
		{#if successMessage}
			<div class="mb-6 bg-green-50 border border-green-200 rounded-lg p-4">
				<div class="flex items-center">
					<svg class="w-5 h-5 text-green-400 mr-2" fill="currentColor" viewBox="0 0 20 20">
						<path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"/>
					</svg>
					<span class="text-green-800">{successMessage}</span>
				</div>
			</div>
		{/if}

		{#if error}
			<div class="mb-6 bg-red-50 border border-red-200 rounded-lg p-4">
				<div class="flex items-center">
					<svg class="w-5 h-5 text-red-400 mr-2" fill="currentColor" viewBox="0 0 20 20">
						<path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd"/>
					</svg>
					<span class="text-red-800">{error}</span>
				</div>
			</div>		{/if}

		<!-- Main Content Grid -->
		<div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
			<!-- Profile Avatar and Quick Actions -->
			<div class="lg:col-span-1">
				<div class="bg-white rounded-lg shadow-md p-6">
					<div class="text-center">
						<!-- Avatar -->
						<div class="mx-auto w-24 h-24 bg-gray-200 rounded-full flex items-center justify-center mb-4">
							<svg class="w-12 h-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
								<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/>
							</svg>
						</div>

						<h3 class="text-lg font-medium text-gray-900">{user.fullName || user.username}</h3>
						<p class="text-sm text-gray-500">{user.email}</p>
						<div class="mt-1">
							<span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
								{user.role === 'KH' ? 'Khách hàng' : user.role === 'Nvien' ? 'Nhân viên' : 'Quản lý'}
							</span>
						</div>

						<!-- Upload Avatar Button -->
						<div class="mt-4">
							<label for="avatar-upload" class="cursor-pointer inline-flex items-center px-3 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 hover:bg-gray-50 transition-colors duration-200">
								<svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
									<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12"/>
								</svg>
								Đổi ảnh đại diện
							</label>
							<input 
								id="avatar-upload" 
								type="file" 
								accept="image/*" 
								class="hidden" 
								on:change={uploadAvatar}
							>
						</div>					</div>
				</div>
			</div>
			<!-- Profile Information -->
			<div class="lg:col-span-2">
				<div class="bg-white rounded-lg shadow-md">
					<div class="px-6 py-4 border-b border-gray-200">
						<div class="flex items-center justify-between">
							<h2 class="text-lg font-medium text-gray-900">Thông tin cá nhân</h2>
							{#if !isEditing}
								<button 
									on:click={startEdit}
									class="inline-flex items-center px-3 py-2 border border-blue-600 rounded-md text-sm font-medium text-blue-600 hover:bg-blue-50 transition-colors duration-200"
								>
									<svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
										<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"/>
									</svg>
									Chỉnh sửa
								</button>
							{/if}
						</div>
					</div>

					<div class="p-6">
						{#if isEditing}
							<!-- Edit Form -->
							<form on:submit|preventDefault={saveProfile} class="space-y-6">
								<div class="grid grid-cols-1 md:grid-cols-2 gap-6">
									<div>
										<label for="username" class="block text-sm font-medium text-gray-700 mb-1">
											Tên đăng nhập
										</label>
										<input 
											id="username"
											type="text" 
											bind:value={editForm.username}
											class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
											required
										>
									</div>

									<div>
										<label for="fullName" class="block text-sm font-medium text-gray-700 mb-1">
											Họ và tên
										</label>
										<input 
											id="fullName"
											type="text" 
											bind:value={editForm.fullName}
											class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
											required
										>
									</div>

									<div>
										<label for="email" class="block text-sm font-medium text-gray-700 mb-1">
											Email
										</label>
										<input 
											id="email"
											type="email" 
											bind:value={editForm.email}
											class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
											required
										>
									</div>

									<div>
										<label for="phone" class="block text-sm font-medium text-gray-700 mb-1">
											Số điện thoại
										</label>
										<input 
											id="phone"
											type="tel" 
											bind:value={editForm.phone}
											class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
										>
									</div>
								</div>								<div>
									<label for="address" class="block text-sm font-medium text-gray-700 mb-1">
										Địa chỉ
									</label>
									<textarea 
										id="address"
										bind:value={editForm.address}
										rows="3"
										class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
										placeholder="Nhập địa chỉ của bạn..."
									></textarea>
								</div>

								<!-- Password Section -->
								<div class="border-t border-gray-200 pt-6">
									<h4 class="text-sm font-medium text-gray-900 mb-4">Đổi mật khẩu (tùy chọn)</h4>
									<div class="grid grid-cols-1 md:grid-cols-2 gap-6">
										<div>
											<label for="password" class="block text-sm font-medium text-gray-700 mb-1">
												Mật khẩu mới
											</label>
											<input 
												id="password"
												type="password" 
												bind:value={editForm.password}
												class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
												placeholder="Để trống nếu không muốn đổi"
											>
										</div>

										<div>
											<label for="confirmPassword" class="block text-sm font-medium text-gray-700 mb-1">
												Xác nhận mật khẩu
											</label>
											<input 
												id="confirmPassword"
												type="password" 
												bind:value={editForm.confirmPassword}
												class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
												placeholder="Nhập lại mật khẩu mới"
											>
										</div>
									</div>
									<p class="mt-2 text-sm text-gray-500">
										Mật khẩu phải có ít nhất 6 ký tự. Để trống nếu không muốn thay đổi mật khẩu.
									</p>
								</div>

								<!-- Form Actions -->
								<div class="flex justify-end space-x-3">
									<button 
										type="button"
										on:click={cancelEdit}
										disabled={loading}
										class="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 hover:bg-gray-50 transition-colors duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
									>
										Hủy
									</button>
									<button 
										type="submit"
										disabled={loading}
										class="px-4 py-2 bg-blue-600 text-white rounded-md text-sm font-medium hover:bg-blue-700 transition-colors duration-200 disabled:opacity-50 disabled:cursor-not-allowed flex items-center"
									>
										{#if loading}
											<svg class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
												<circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
												<path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
											</svg>
											Đang lưu...
										{:else}
											Lưu thay đổi
										{/if}
									</button>
								</div>
							</form>
						{:else}
							<!-- View Mode -->
							<div class="space-y-6">
								<div class="grid grid-cols-1 md:grid-cols-2 gap-6">
									<div>
										<span class="block text-sm font-medium text-gray-500">Tên đăng nhập</span>
										<p class="mt-1 text-sm text-gray-900">{user.username}</p>
									</div>

									<div>
										<span class="block text-sm font-medium text-gray-500">Họ và tên</span>
										<p class="mt-1 text-sm text-gray-900">{user.fullName || 'Chưa cập nhật'}</p>
									</div>

									<div>
										<span class="block text-sm font-medium text-gray-500">Email</span>
										<p class="mt-1 text-sm text-gray-900">{user.email}</p>
									</div>

									<div>
										<span class="block text-sm font-medium text-gray-500">Số điện thoại</span>
										<p class="mt-1 text-sm text-gray-900">{user.phone || 'Chưa cập nhật'}</p>
									</div>

									<div class="md:col-span-2">
										<span class="block text-sm font-medium text-gray-500">Địa chỉ</span>
										<p class="mt-1 text-sm text-gray-900">{user.address || 'Chưa cập nhật'}</p>
									</div>
								</div>
							</div>
						{/if}
					</div>
				</div>
			</div>
		</div>
	{/if}
</div>
