<script>
    import { goto } from '$app/navigation';

    let username = '';
    let password = '';
    let email = '';
    let fullName = '';
    let phone = '';
    let address = '';
    let error = '';
    let success = '';

    async function handleRegistry() {
        const response = await fetch('http://localhost:8080/api/auth/registry', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password, email, fullName, phone, address })
        });

        if (response.ok) {
            const data = await response.json();
            success = data.message;
            error = '';
            setTimeout(() => goto('/auth/login'), 2000); // Chuyển hướng sau 2 giây
        } else {
            const data = await response.json();
            error = data.error || 'Đăng ký thất bại';
            success = '';
        }
    }
</script>

<h1>Đăng ký</h1>
<div>
    <input type="text" bind:value={username} placeholder="Tên người dùng" required />
    <input type="password" bind:value={password} placeholder="Mật khẩu" required />
    <input type="email" bind:value={email} placeholder="Email" required />
    <input type="text" bind:value={fullName} placeholder="Họ tên" />
    <input type="text" bind:value={phone} placeholder="Số điện thoại" />
    <input type="text" bind:value={address} placeholder="Địa chỉ" />
    <button on:click={handleRegistry}>Đăng ký</button>
</div>
{#if error}
    <p style="color: red;">{error}</p>
{/if}
{#if success}
    <p style="color: green;">{success}</p>
{/if}