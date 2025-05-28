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

export interface Product {
	bookId: number;
	title: string;
	author?: string;
	publisher?: string;
	publicationYear?: number;
	description?: string;
	price: number;
	categoryIds?: number[];
	categoryNames?: string[];
	primaryImageUrl?: string;
	imageUrls?: string[];
	stockQuantity?: number;
}

export interface ProductFormData {
	title: string;
	author: string;
	publisher: string;
	publicationYear: number | null;
	description: string;
	price: number | null;
	categoryIds: number[];
	primaryImageUrl: string;
}

export interface Category {
	categoryId: number;
	categoryName: string;
}

export interface ApiResponse<T = any> {
	success: boolean;
	data?: T;
	message: string;
}
