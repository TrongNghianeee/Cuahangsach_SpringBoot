<script lang="ts">
	import { createEventDispatcher } from 'svelte';
	import type { BookImage } from '$lib/types';
	import { getImageUrl } from '$lib/imageUtils';
	import { uploadBookImage, setPrimaryImage, deleteBookImage } from '$lib/bookImageService';

	export let bookId: number;
	export let images: BookImage[] = [];
	export let isOpen: boolean = false;

	const dispatch = createEventDispatcher();

	let loading = false;
	let uploadLoading = false;
	let error = '';
	let success = '';
	let dragOver = false;

	// File input
	let fileInput: HTMLInputElement;
	let selectedFile: File | null = null;
	let description = '';
	let isPrimary = false;

	function closeModal() {
		isOpen = false;
		dispatch('close');
	}

	function handleFileSelect(event: Event) {
		const input = event.target as HTMLInputElement;
		if (input.files && input.files[0]) {
			selectedFile = input.files[0];
			validateFile();
		}
	}

	function handleDrop(event: DragEvent) {
		event.preventDefault();
		dragOver = false;
		
		if (event.dataTransfer?.files && event.dataTransfer.files[0]) {
			selectedFile = event.dataTransfer.files[0];
			validateFile();
		}
	}

	function handleDragOver(event: DragEvent) {
		event.preventDefault();
		dragOver = true;
	}

	function handleDragLeave() {
		dragOver = false;
	}

	function validateFile() {
		if (!selectedFile) return;

		error = '';
		
		if (!selectedFile.type.startsWith('image/')) {
			error = 'File must be an image';
			selectedFile = null;
			return;
		}

		const maxSize = 10 * 1024 * 1024; // 10MB
		if (selectedFile.size > maxSize) {
			error = 'File size must be less than 10MB';
			selectedFile = null;
			return;
		}
	}

	async function uploadImage() {
		if (!selectedFile) return;

		uploadLoading = true;
		error = '';
		success = '';

		try {
			const newImage = await uploadBookImage(bookId, selectedFile, description, isPrimary);
			images = [...images, newImage].sort((a, b) => {
				// Sort by isPrimary first, then by uploadedAt
				if (a.isPrimary && !b.isPrimary) return -1;
				if (!a.isPrimary && b.isPrimary) return 1;
				return new Date(b.uploadedAt || '').getTime() - new Date(a.uploadedAt || '').getTime();
			});

			success = 'Image uploaded successfully!';
			resetForm();
			dispatch('uploaded', newImage);
		} catch (err) {
			error = err instanceof Error ? err.message : 'Failed to upload image';
		} finally {
			uploadLoading = false;
		}
	}

	async function handleSetPrimary(imageId: number) {
		loading = true;
		error = '';

		try {
			await setPrimaryImage(imageId);
			
			// Update local images array
			images = images.map(img => ({
				...img,
				isPrimary: img.imageId === imageId
			}));

			success = 'Primary image set successfully!';
			dispatch('primarySet', imageId);
		} catch (err) {
			error = err instanceof Error ? err.message : 'Failed to set primary image';
		} finally {
			loading = false;
		}
	}

	async function handleDeleteImage(imageId: number) {
		if (!confirm('Are you sure you want to delete this image?')) return;

		loading = true;
		error = '';

		try {
			await deleteBookImage(imageId);
			images = images.filter(img => img.imageId !== imageId);
			success = 'Image deleted successfully!';
			dispatch('deleted', imageId);
		} catch (err) {
			error = err instanceof Error ? err.message : 'Failed to delete image';
		} finally {
			loading = false;
		}
	}

	function resetForm() {
		selectedFile = null;
		description = '';
		isPrimary = false;
		if (fileInput) {
			fileInput.value = '';
		}
	}

	function formatFileSize(bytes: number): string {
		if (bytes === 0) return '0 Bytes';
		const k = 1024;
		const sizes = ['Bytes', 'KB', 'MB', 'GB'];
		const i = Math.floor(Math.log(bytes) / Math.log(k));
		return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
	}

	// Clear messages after 3 seconds
	$: if (success) {
		setTimeout(() => success = '', 3000);
	}
	$: if (error) {
		setTimeout(() => error = '', 5000);
	}
</script>

{#if isOpen}
	<div class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50">
		<div class="max-w-4xl w-full max-h-[90vh] overflow-y-auto bg-white rounded-lg shadow-xl m-4">
			<!-- Header -->
			<div class="flex items-center justify-between p-6 border-b">
				<h2 class="text-xl font-bold text-gray-900">Quản lý ảnh sản phẩm</h2>
				<button 
					on:click={closeModal}
					class="p-2 text-gray-400 hover:text-gray-600 rounded-lg hover:bg-gray-100"
				>
					<svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
						<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
					</svg>
				</button>
			</div>

			<!-- Messages -->
			{#if success}
				<div class="mx-6 mt-4 p-3 bg-green-100 border border-green-400 text-green-700 rounded">
					{success}
				</div>
			{/if}

			{#if error}
				<div class="mx-6 mt-4 p-3 bg-red-100 border border-red-400 text-red-700 rounded">
					{error}
				</div>
			{/if}

			<!-- Upload Section -->
			<div class="p-6 border-b">
				<h3 class="text-lg font-semibold mb-4">Upload ảnh mới</h3>
				
				<!-- File Upload Area -->
				<div 
					class="border-2 border-dashed rounded-lg p-6 text-center transition-colors
						{dragOver ? 'border-blue-400 bg-blue-50' : 'border-gray-300 hover:border-gray-400'}"
					on:drop={handleDrop}
					on:dragover={handleDragOver}
					on:dragleave={handleDragLeave}
				>
					{#if selectedFile}
						<div class="space-y-2">
							<p class="text-sm font-medium">{selectedFile.name}</p>
							<p class="text-xs text-gray-500">{formatFileSize(selectedFile.size)}</p>
							<button 
								on:click={() => { selectedFile = null; if (fileInput) fileInput.value = ''; }}
								class="text-sm text-red-600 hover:text-red-800"
							>
								Xóa file
							</button>
						</div>
					{:else}
						<div class="space-y-2">
							<svg class="mx-auto h-12 w-12 text-gray-400" stroke="currentColor" fill="none" viewBox="0 0 48 48">
								<path d="M28 8H12a4 4 0 00-4 4v20m32-12v8m0 0v8a4 4 0 01-4 4H12a4 4 0 01-4-4v-4m32-4l-3.172-3.172a4 4 0 00-5.656 0L28 28M8 32l9.172-9.172a4 4 0 015.656 0L28 28m0 0l4 4m4-24h8m-4-4v8m-12 4h.02" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
							</svg>
							<div class="flex text-sm text-gray-600">
								<label class="relative cursor-pointer bg-white rounded-md font-medium text-indigo-600 hover:text-indigo-500 focus-within:outline-none">
									<span>Upload a file</span>
									<input bind:this={fileInput} type="file" class="sr-only" accept="image/*" on:change={handleFileSelect} />
								</label>
								<p class="pl-1">or drag and drop</p>
							</div>
							<p class="text-xs text-gray-500">PNG, JPG, GIF up to 10MB</p>
						</div>
					{/if}
				</div>

				{#if selectedFile}
					<!-- Upload Form -->
					<div class="mt-4 space-y-4">
						<div>
							<label class="block text-sm font-medium text-gray-700 mb-1">Description (optional)</label>
							<input 
								bind:value={description}
								type="text" 
								class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
								placeholder="Enter image description..."
							/>
						</div>

						<div class="flex items-center">
							<input 
								bind:checked={isPrimary}
								type="checkbox" 
								id="isPrimary"
								class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
							/>
							<label for="isPrimary" class="ml-2 block text-sm text-gray-700">
								Set as primary image
							</label>
						</div>

						<button 
							on:click={uploadImage}
							disabled={uploadLoading || !selectedFile}
							class="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50"
						>
							{#if uploadLoading}
								<svg class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" fill="none" viewBox="0 0 24 24">
									<circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
									<path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
								</svg>
								Uploading...
							{:else}
								Upload Image
							{/if}
						</button>
					</div>
				{/if}
			</div>

			<!-- Images Grid -->
			<div class="p-6">
				<h3 class="text-lg font-semibold mb-4">Ảnh hiện tại ({images.length})</h3>
				
				{#if images.length === 0}
					<div class="text-center py-8 text-gray-500">
						<svg class="mx-auto h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
							<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
						</svg>
						<p class="mt-2">Chưa có ảnh nào</p>
					</div>
				{:else}
					<div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
						{#each images as image}
							<div class="relative group border rounded-lg overflow-hidden">
								<!-- Primary badge -->
								{#if image.isPrimary}
									<div class="absolute top-2 left-2 z-10">
										<span class="bg-green-500 text-white text-xs px-2 py-1 rounded-full">
											Primary
										</span>
									</div>
								{/if}

								<!-- Image -->
								<div class="aspect-square bg-gray-100 flex items-center justify-center">
									<img 
										src={getImageUrl(image.imageUrl)}
										alt={image.description || 'Product image'}
										class="max-w-full max-h-full object-contain"
									/>
								</div>

								<!-- Actions -->
								<div class="absolute inset-0 bg-black bg-opacity-0 group-hover:bg-opacity-50 transition-opacity flex items-center justify-center opacity-0 group-hover:opacity-100">
									<div class="space-x-2">
										{#if !image.isPrimary}
											<button 
												on:click={() => handleSetPrimary(image.imageId)}
												disabled={loading}
												class="p-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors"
												title="Set as primary"
											>
												<svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
													<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
												</svg>
											</button>
										{/if}

										<button 
											on:click={() => handleDeleteImage(image.imageId)}
											disabled={loading}
											class="p-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors"
											title="Delete image"
										>
											<svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
												<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
											</svg>
										</button>
									</div>
								</div>

								<!-- Description -->
								{#if image.description}
									<div class="p-2 bg-white">
										<p class="text-xs text-gray-600 truncate">{image.description}</p>
									</div>
								{/if}
							</div>
						{/each}
					</div>
				{/if}
			</div>

			<!-- Footer -->
			<div class="p-6 border-t bg-gray-50 flex justify-end">
				<button 
					on:click={closeModal}
					class="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
				>
					Close
				</button>
			</div>
		</div>
	</div>
{/if}
