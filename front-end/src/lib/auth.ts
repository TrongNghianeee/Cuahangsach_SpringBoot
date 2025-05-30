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

export interface CurrentUser {
    userId: number;
    username: string;
    email: string;
    fullName: string;
    phone: string;
    address: string;
    role: string;
    status: string;
}

export async function getCurrentUser(): Promise<CurrentUser | null> {
    const userToken = getToken();
    if (!userToken) {
        return null;
    }

    try {
        const response = await fetch('http://localhost:8080/api/auth/me', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${userToken}`,
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const userData = await response.json();
            return userData;
        } else {
            // Token might be invalid, clear it
            logout();
            return null;
        }
    } catch (error) {
        console.error('Error fetching current user:', error);
        return null;
    }
}

export async function getCurrentUserId(): Promise<number | null> {
    const user = await getCurrentUser();
    return user ? user.userId : null;
}
