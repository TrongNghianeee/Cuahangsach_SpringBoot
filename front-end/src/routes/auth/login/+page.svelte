<script lang="ts">
	import { goto } from '$app/navigation';
	import { token } from '$lib/stores';

	let username = '';
	let password = '';
	let error = '';
	let loading = false;

	async function handleLogin() {
		if (!username.trim() || !password.trim()) {
			error = 'Vui lòng nhập đầy đủ thông tin';
			return;
		}

		loading = true;
		error = '';

		try {
			const response = await fetch('http://localhost:8080/api/auth/login', {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json'
				},
				body: JSON.stringify({ username, password })
			});

			if (response.ok) {
				const data = await response.json();
				localStorage.setItem('token', data.access_token); // Lưu vào localStorage
				token.set(data.access_token); // Set vào store
				
				// Decode JWT to get user role (basic decoding without verification)
				try {
					const payload = JSON.parse(atob(data.access_token.split('.')[1]));
					const userRole = payload.role || payload.authorities?.[0] || 'KH';
					
					// Redirect based on role
					if (userRole === 'Qly' || userRole === 'ROLE_ADMIN') {
						goto('/admin');
					} else {
						goto('/');
					}
				} catch (e) {
					// If JWT decode fails, just go to home
					goto('/');
				}
			} else {
				const errorData = await response.json();
				error = errorData.detail || 'Tên người dùng hoặc mật khẩu không đúng'; // Hiển thị lỗi chi tiết từ server
			}
		} catch (e) {
			error = 'Lỗi kết nối. Vui lòng thử lại.';
		} finally {
			loading = false;
		}
	}
	function handleKeyPress(event: KeyboardEvent) {
		if (event.key === 'Enter') {
			handleLogin();
		}
	}
</script>

<div class="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
	<div class="max-w-md w-full space-y-8">
		<div class="text-center">
			<div class="mx-auto h-16 w-16 bg-blue-600 rounded-full flex items-center justify-center mb-4">
				<svg class="h-8 w-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
					<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.746 0 3.332.477 4.5 1.253v13C19.832 18.477 18.246 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"/>
				</svg>
			</div>
			<h2 class="text-3xl font-bold text-gray-900 mb-2">Bookstore</h2>
			<p class="text-gray-600">Đăng nhập vào tài khoản của bạn</p>
		</div>

		<div class="bg-white rounded-lg shadow-lg p-8">
			{#if error}
				<div class="mb-4 p-4 bg-red-100 border border-red-400 text-red-700 rounded-md">
					{error}
				</div>
			{/if}

			<form on:submit|preventDefault={handleLogin} class="space-y-6">
				<div>
					<label for="username" class="block text-sm font-medium text-gray-700 mb-2">
						Tên người dùng
					</label>
					<input
						id="username"
						type="text"
						bind:value={username}
						on:keypress={handleKeyPress}
						class="w-full px-3 py-3 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
						placeholder="Nhập tên người dùng"
						required
						disabled={loading}
					/>
				</div>

				<div>
					<label for="password" class="block text-sm font-medium text-gray-700 mb-2">
						Mật khẩu
					</label>
					<input
						id="password"
						type="password"
						bind:value={password}
						on:keypress={handleKeyPress}
						class="w-full px-3 py-3 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
						placeholder="Nhập mật khẩu"
						required
						disabled={loading}
					/>
				</div>

				<button
					type="submit"
					disabled={loading}
					class="w-full flex justify-center py-3 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed"
				>
					{#if loading}
						<svg class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
							<circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
							<path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
						</svg>
						Đang đăng nhập...
					{:else}
						Đăng nhập
					{/if}
				</button>
			</form>

			<div class="mt-6 text-center">
				<p class="text-sm text-gray-600">
					Chưa có tài khoản? 
					<a href="/auth/register" class="font-medium text-blue-600 hover:text-blue-500 hover:underline">
						Đăng ký ngay
					</a>
				</p>
			</div>
		</div>
	</div>
</div>
