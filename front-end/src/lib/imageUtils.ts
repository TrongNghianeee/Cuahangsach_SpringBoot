// Image utility functions for handling image URLs from backend

const API_BASE_URL = 'http://localhost:8080';

/**
 * Convert backend image URL to frontend accessible URL
 * Now prioritizes uploads folder over static images
 */
export function getImageUrl(imageUrl: string): string {
    if (!imageUrl) {
        return '/placeholder-book.jpg'; // Default placeholder image
    }
    
    // If it's already a full URL, return as is
    if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
        return imageUrl;
    }
    
    // Clean the path - remove leading slashes and folder prefixes
    let cleanPath = imageUrl;
    if (cleanPath.startsWith('/')) {
        cleanPath = cleanPath.substring(1);
    }
    if (cleanPath.startsWith('uploads/')) {
        cleanPath = cleanPath.substring(8);
    }
    if (cleanPath.startsWith('static/img/')) {
        cleanPath = cleanPath.substring(11);
    }
    if (cleanPath.startsWith('img/')) {
        cleanPath = cleanPath.substring(4);
    }
    
    // All images now go through single endpoint that checks uploads first, then static
    return `${API_BASE_URL}/api/images/${cleanPath}`;
}

/**
 * Get multiple image URLs for a book
 */
export function getBookImageUrls(images: any[]): string[] {
    if (!images || images.length === 0) {
        return [getImageUrl('')]; // Return placeholder
    }
    
    return images.map(img => getImageUrl(img.imageUrl));
}

/**
 * Get primary image URL for a book
 */
export function getPrimaryImageUrl(images: any[]): string {
    if (!images || images.length === 0) {
        return getImageUrl('');
    }
    
    // Find primary image
    const primaryImage = images.find(img => img.isPrimary);
    if (primaryImage) {
        return getImageUrl(primaryImage.imageUrl);
    }
    
    // If no primary, use first image
    return getImageUrl(images[0].imageUrl);
}
