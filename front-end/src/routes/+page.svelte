<script>
	import { onMount } from 'svelte';
	import { goto } from '$app/navigation';
	import { token } from '$lib/stores';

	onMount(async () => {
		const storedToken = localStorage.getItem('token');
		if (storedToken) {
			token.set(storedToken);
			const response = await fetch('http://localhost:8080/api/auth/me', {
				headers: {
					Authorization: `Bearer ${storedToken}`
				}
			});
			if (response.ok) {
				const user = await response.json();
				if (user.role === 'Qly') {
					goto('/admin');
				} else if (user.role === 'KH') {
					goto('/user');
				}
			} else {
				token.set('');
				localStorage.removeItem('token');
				goto('/auth/login');
			}
		} else {
			goto('/auth/login');
		}
	});
</script>

<h1>Chào mừng đến với Bookstore</h1>
<p>Vui lòng đăng nhập để tiếp tục.</p>
