import type { BookImage, ApiResponse } from './types';

const API_BASE_URL = 'http://localhost:8080/api/user/book-images';

/**
 * Get all images for a book (public access)
 */
export async function getUserBookImages(bookId: number): Promise<BookImage[]> {
    try {
        const response = await fetch(`${API_BASE_URL}/book/${bookId}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error(`Failed to fetch images: ${response.status}`);
        }

        const result: ApiResponse<BookImage[]> = await response.json();
        
        if (result.success && result.data) {
            return result.data;
        } else {
            return []; // Return empty array instead of throwing error
        }
    } catch (error) {
        console.warn('Error fetching book images:', error);
        return []; // Return empty array on error
    }
}

/**
 * Get primary image for a book (public access)
 */
export async function getUserPrimaryImage(bookId: number): Promise<BookImage | null> {
    try {
        const response = await fetch(`${API_BASE_URL}/book/${bookId}/primary`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error(`Failed to fetch primary image: ${response.status}`);
        }

        const result: ApiResponse<BookImage> = await response.json();
        
        if (result.success && result.data) {
            return result.data;
        } else {
            return null;
        }
    } catch (error) {
        console.warn('Error fetching primary image:', error);
        return null;
    }
}
