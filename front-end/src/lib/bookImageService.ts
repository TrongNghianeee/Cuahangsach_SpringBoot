import type { BookImage, ApiResponse } from './types';

const API_BASE_URL = 'http://localhost:8080/api/admin/book-images';

export interface UploadImageResponse {
    filename: string;
    url: string;
}

/**
 * Get all images for a book
 */
export async function getBookImages(bookId: number): Promise<BookImage[]> {
    const token = localStorage.getItem('token');
    
    if (!token) {
        throw new Error('No authentication token found');
    }

    try {
        const response = await fetch(`${API_BASE_URL}/book/${bookId}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error(`Failed to fetch images: ${response.status}`);
        }        const result: ApiResponse<BookImage[]> = await response.json();
        
        if (result.success && result.data) {
            return result.data;
        } else {
            throw new Error(result.message || 'Failed to fetch images');
        }
    } catch (error) {
        console.error('Error fetching book images:', error);
        throw error;
    }
}

/**
 * Upload an image for a book
 */
export async function uploadBookImage(
    bookId: number, 
    file: File, 
    description?: string, 
    isPrimary: boolean = false
): Promise<BookImage> {
    const token = localStorage.getItem('token');
    
    if (!token) {
        throw new Error('No authentication token found');
    }

    // Validate file type
    if (!file.type.startsWith('image/')) {
        throw new Error('File must be an image');
    }

    // Validate file size (10MB max)
    const maxSize = 10 * 1024 * 1024; // 10MB
    if (file.size > maxSize) {
        throw new Error('File size must be less than 10MB');
    }

    const formData = new FormData();
    formData.append('file', file);
    if (description) {
        formData.append('description', description);
    }
    formData.append('isPrimary', isPrimary.toString());

    try {
        const response = await fetch(`${API_BASE_URL}/book/${bookId}/upload`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            body: formData
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Upload failed: ${errorText}`);
        }

        const result: ApiResponse<BookImage> = await response.json();
        
        if (result.success && result.data) {
            return result.data;
        } else {
            throw new Error(result.message || 'Failed to upload image');
        }
    } catch (error) {
        console.error('Error uploading image:', error);
        throw error;
    }
}

/**
 * Set an image as primary
 */
export async function setPrimaryImage(imageId: number): Promise<void> {
    const token = localStorage.getItem('token');
    
    if (!token) {
        throw new Error('No authentication token found');
    }

    try {
        const response = await fetch(`${API_BASE_URL}/${imageId}/set-primary`, {
            method: 'PUT',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Failed to set primary: ${errorText}`);
        }

        const result: ApiResponse<void> = await response.json();
        
        if (!result.success) {
            throw new Error(result.message || 'Failed to set primary image');
        }
    } catch (error) {
        console.error('Error setting primary image:', error);
        throw error;
    }
}

/**
 * Delete an image
 */
export async function deleteBookImage(imageId: number): Promise<void> {
    const token = localStorage.getItem('token');
    
    if (!token) {
        throw new Error('No authentication token found');
    }

    try {
        const response = await fetch(`${API_BASE_URL}/${imageId}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Failed to delete image: ${errorText}`);
        }

        const result: ApiResponse<void> = await response.json();
        
        if (!result.success) {
            throw new Error(result.message || 'Failed to delete image');
        }
    } catch (error) {
        console.error('Error deleting image:', error);
        throw error;
    }
}
