import type { OverviewStats } from './types';

const API_BASE_URL = 'http://localhost:8080/api';

export async function fetchDashboardOverview(): Promise<OverviewStats> {
    const token = localStorage.getItem('token');
    
    if (!token) {
        throw new Error('No authentication token found');
    }

    try {
        const response = await fetch(`${API_BASE_URL}/dashboard/overview`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            const errorText = await response.text();
            if (response.status === 401) {
                throw new Error('Authentication failed - please login again');
            } else if (response.status === 403) {
                throw new Error(`Access denied - Admin role required. Response: ${errorText}`);
            } else {
                throw new Error(`Failed to fetch dashboard data: ${response.status} - ${errorText}`);
            }
        }

        const data: OverviewStats = await response.json();
        return data;
    } catch (error) {
        console.error('Error fetching dashboard overview:', error);
        throw error;
    }
}

// Test function for public endpoint
export async function testPublicEndpoint(): Promise<string> {
    try {
        const response = await fetch(`${API_BASE_URL}/dashboard/public-test`);
        return await response.text();
    } catch (error) {
        console.error('Error testing public endpoint:', error);
        throw error;
    }
}
