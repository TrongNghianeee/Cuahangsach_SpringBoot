// Types for the bookstore application

export interface User {
	userId: number;
	username: string;
	email: string;
	fullName?: string;
	phone?: string;
	address?: string;
	role: 'KH' | 'Nvien' | 'Qly';
	status: 'Active' | 'Lock';
	createdAt?: string;
}

export interface UserFormData {
	username: string;
	password: string;
	email: string;
	fullName: string;
	phone: string;
	address: string;
	role: 'KH' | 'Nvien' | 'Qly';
	status: 'Active' | 'Lock';
}

export interface OverviewStats {
	totalUsers: number;
	totalProducts: number;
	totalOrders: number;
}

export interface Product {
	bookId: number;
	title: string;
	author?: string;
	publisher?: string;
	publicationYear?: number;
	description?: string;
	price: number;
	stockQuantity?: number;
	categories?: Category[];
	images?: BookImage[];
}

export interface ProductFormData {
	title: string;
	author: string;
	publisher: string;
	publicationYear: number | null;
	description: string;
	price: number | null;
	stockQuantity: number;
	categoryIds: number[];
}

export interface BookImage {
	imageId: number;
	imageUrl: string;
	isPrimary: boolean;
}

export interface Category {
	categoryId: number;
	categoryName: string;
}

export interface InventoryDTO {
	transactionId?: number;
	bookId: number;
	transactionType: 'Nhập' | 'Xuất';
	quantity: number;
	price: number;
	userId: number;
	transactionDate?: string;
	// Additional info for display
	bookTitle?: string;
	username?: string;
}

export interface ShoppingCartItem {
	userId: number;
	bookId: number;
	bookTitle: string;
	bookAuthor?: string;
	bookPublisher?: string;
	bookDescription?: string;
	bookPrice: number;
	stockQuantity: number;
}

export interface ApiResponse<T = any> {
	success: boolean;
	data?: T;
	message: string;
}
