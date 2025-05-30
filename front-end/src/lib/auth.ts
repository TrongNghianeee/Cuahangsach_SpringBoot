import { goto } from '$app/navigation';
import { token } from './stores';

export async function logout(): Promise<void> {
    const userToken = localStorage.getItem('token');
    
    if (userToken) {
        try {
            const response = await fetch('http://localhost:8080/api/auth/logout', {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${userToken}`,
                    'Content-Type': 'application/json'
                }
            });
            
            if (!response.ok) {
                console.warn('Logout API call failed, but proceeding with client-side logout');
            }
        } catch (error) {
            console.error('Logout error:', error);
            // Even if the API call fails, we should still clear the local token
        }
    }
    
    // Clear token from localStorage and store
    localStorage.removeItem('token');
    token.set('');
    
    // Redirect to login page
    goto('/auth/login');
}

export function isAuthenticated(): boolean {
    const userToken = localStorage.getItem('token');
    return !!userToken;
}

export function getToken(): string | null {
    return localStorage.getItem('token');
}
