<script>
	import { goto } from '$app/navigation';
	import { token } from '$lib/stores';

	let username = '';
	let password = '';
	let error = '';
	async function handleLogin() {
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
	}
</script>

<h1>Đăng nhập</h1>
<div>
	<input type="text" bind:value={username} placeholder="Tên người dùng" required />
	<input type="password" bind:value={password} placeholder="Mật khẩu" required />
	<button on:click={handleLogin}>Đăng nhập</button>
</div>
{#if error}
	<p style="color: red;">{error}</p>
{/if}
