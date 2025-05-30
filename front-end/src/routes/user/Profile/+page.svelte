<!-- front-end/src/routes/user/Profile/+page.svelte -->
<script lang="ts">
	// Mock user data for demonstration
	let user = {
		id: 1,
		username: "nguyenvana",
		email: "nguyenvana@email.com",
		fullName: "Nguyễn Văn A",
		phone: "0123456789",
		address: "123 Đường ABC, Quận 1, TP.HCM",
		birthDate: "1990-01-15",
		avatar: null
	};

	let isEditing = false;
	let editForm = { ...user };

	function startEdit() {
		isEditing = true;
		editForm = { ...user };
	}

	function cancelEdit() {
		isEditing = false;
		editForm = { ...user };
	}

	function saveProfile() {
		// Here you would typically send the data to your API
		user = { ...editForm };
		isEditing = false;
		// Show success message
		alert('Cập nhật thông tin thành công!');
	}

	function changePassword() {
		// Handle password change
		alert('Chức năng đổi mật khẩu sẽ được phát triển!');
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

	<div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
		<!-- Profile Avatar and Quick Actions -->
		<div class="lg:col-span-1">
			<div class="bg-white rounded-lg shadow-md p-6">
				<div class="text-center">
					<!-- Avatar -->
					<div class="mx-auto w-24 h-24 bg-gray-200 rounded-full flex items-center justify-center mb-4">
						{#if user.avatar}
							<img src={user.avatar} alt="Avatar" class="w-24 h-24 rounded-full object-cover">
						{:else}
							<svg class="w-12 h-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
								<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/>
							</svg>
						{/if}
					</div>

					<h3 class="text-lg font-medium text-gray-900">{user.fullName}</h3>
					<p class="text-sm text-gray-500">{user.email}</p>

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
					</div>
				</div>

				<!-- Quick Actions -->
				<div class="mt-6 space-y-3">
					<button 
						on:click={changePassword}
						class="w-full flex items-center justify-center px-4 py-2 border border-red-300 rounded-md text-sm font-medium text-red-700 hover:bg-red-50 transition-colors duration-200"
					>
						<svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
							<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"/>
						</svg>
						Đổi mật khẩu
					</button>
				</div>
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

								<div>
									<label for="birthDate" class="block text-sm font-medium text-gray-700 mb-1">
										Ngày sinh
									</label>
									<input 
										id="birthDate"
										type="date" 
										bind:value={editForm.birthDate}
										class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
									>
								</div>
							</div>

							<div>
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

							<!-- Form Actions -->
							<div class="flex justify-end space-x-3">
								<button 
									type="button"
									on:click={cancelEdit}
									class="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 hover:bg-gray-50 transition-colors duration-200"
								>
									Hủy
								</button>
								<button 
									type="submit"
									class="px-4 py-2 bg-blue-600 text-white rounded-md text-sm font-medium hover:bg-blue-700 transition-colors duration-200"
								>
									Lưu thay đổi
								</button>
							</div>
						</form>
					{:else}						<!-- View Mode -->
						<div class="space-y-6">
							<div class="grid grid-cols-1 md:grid-cols-2 gap-6">
								<div>
									<span class="block text-sm font-medium text-gray-500">Tên đăng nhập</span>
									<p class="mt-1 text-sm text-gray-900">{user.username}</p>
								</div>

								<div>
									<span class="block text-sm font-medium text-gray-500">Họ và tên</span>
									<p class="mt-1 text-sm text-gray-900">{user.fullName}</p>
								</div>

								<div>
									<span class="block text-sm font-medium text-gray-500">Email</span>
									<p class="mt-1 text-sm text-gray-900">{user.email}</p>
								</div>

								<div>
									<span class="block text-sm font-medium text-gray-500">Số điện thoại</span>
									<p class="mt-1 text-sm text-gray-900">{user.phone || 'Chưa cập nhật'}</p>
								</div>

								<div>
									<span class="block text-sm font-medium text-gray-500">Ngày sinh</span>
									<p class="mt-1 text-sm text-gray-900">
										{user.birthDate ? new Date(user.birthDate).toLocaleDateString('vi-VN') : 'Chưa cập nhật'}
									</p>
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
</div>
