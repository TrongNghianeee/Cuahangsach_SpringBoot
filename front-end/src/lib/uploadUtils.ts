// Upload utility functions

const API_BASE_URL = 'http://localhost:8080/api';

export interface UploadResponse {
    filename: string;
    url: string;
}

/**
 * Upload an image file to the server
 */
export async function uploadImage(file: File): Promise<UploadResponse> {
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

    try {
        const response = await fetch(`${API_BASE_URL}/upload/image`, {
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

        const result: UploadResponse = await response.json();
        return result;
    } catch (error) {
        console.error('Error uploading image:', error);
        throw error;
    }
}

/**
 * Validate image file before upload
 */
export function validateImageFile(file: File): string | null {
    // Check file type
    if (!file.type.startsWith('image/')) {
        return 'File must be an image';
    }

    // Check file size (10MB max)
    const maxSize = 10 * 1024 * 1024; // 10MB
    if (file.size > maxSize) {
        return 'File size must be less than 10MB';
    }

    // Check file extensions
    const allowedExtensions = ['.jpg', '.jpeg', '.png', '.gif', '.webp'];
    const fileName = file.name.toLowerCase();
    const hasValidExtension = allowedExtensions.some(ext => fileName.endsWith(ext));
    
    if (!hasValidExtension) {
        return 'File must be a valid image format (JPG, PNG, GIF, WebP)';
    }

    return null; // No errors
}

/**
 * Format file size for display
 */
export function formatFileSize(bytes: number): string {
    if (bytes === 0) return '0 Bytes';
    
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}
