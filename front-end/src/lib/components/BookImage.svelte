<script lang="ts">
    import { getImageUrl, getPrimaryImageUrl } from '$lib/imageUtils';
    
    export let images: any[] = [];
    export let alt: string = 'Book image';
    export let className: string = '';
    export let showPrimaryOnly: boolean = true;
    
    let imageError = false;
    let currentImageIndex = 0;
    
    $: imageUrls = images.map(img => getImageUrl(img.imageUrl));
    $: primaryImageUrl = getPrimaryImageUrl(images);
    $: displayUrl = showPrimaryOnly ? primaryImageUrl : (imageUrls[currentImageIndex] || primaryImageUrl);
    
    function handleImageError() {
        console.warn('Failed to load image:', displayUrl);
        imageError = true;
    }
    
    function nextImage() {
        if (imageUrls.length > 1) {
            currentImageIndex = (currentImageIndex + 1) % imageUrls.length;
            imageError = false;
        }
    }
    
    function prevImage() {
        if (imageUrls.length > 1) {
            currentImageIndex = currentImageIndex === 0 ? imageUrls.length - 1 : currentImageIndex - 1;
            imageError = false;
        }
    }
</script>

<div class="book-image-container {className}">
    {#if imageError}
        <!-- Placeholder when image fails to load -->
        <div class="placeholder-image bg-gray-200 flex items-center justify-center text-gray-400">
            <svg class="w-12 h-12" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" 
                      d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
            </svg>
        </div>
    {:else}
        <img 
            src={displayUrl}
            {alt}
            class="book-image {className}"
            on:error={handleImageError}
            loading="lazy"
        />
    {/if}
    
    {#if !showPrimaryOnly && imageUrls.length > 1}
        <!-- Image navigation -->
        <div class="image-navigation">
            <button 
                class="nav-btn prev-btn"
                on:click={prevImage}
                title="Previous image"
            >
                &#8249;
            </button>
            
            <div class="image-indicators">
                {#each imageUrls as _, index}
                    <button 
                        class="indicator {index === currentImageIndex ? 'active' : ''}"
                        on:click={() => { currentImageIndex = index; imageError = false; }}
                    />
                {/each}
            </div>
            
            <button 
                class="nav-btn next-btn"
                on:click={nextImage}
                title="Next image"
            >
                &#8250;
            </button>
        </div>
    {/if}
</div>

<style>
    .book-image-container {
        position: relative;
        display: inline-block;
    }
    
    .book-image {
        max-width: 100%;
        height: auto;
        border-radius: 0.5rem;
        box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
    }
    
    .placeholder-image {
        min-height: 200px;
        border-radius: 0.5rem;
        border: 2px dashed #e5e7eb;
    }
    
    .image-navigation {
        position: absolute;
        bottom: 8px;
        left: 50%;
        transform: translateX(-50%);
        display: flex;
        align-items: center;
        gap: 8px;
        background: rgba(0, 0, 0, 0.7);
        padding: 4px 8px;
        border-radius: 20px;
    }
    
    .nav-btn {
        background: none;
        border: none;
        color: white;
        font-size: 18px;
        cursor: pointer;
        padding: 2px 6px;
        border-radius: 4px;
        transition: background-color 0.2s;
    }
    
    .nav-btn:hover {
        background: rgba(255, 255, 255, 0.2);
    }
    
    .image-indicators {
        display: flex;
        gap: 4px;
    }
    
    .indicator {
        width: 8px;
        height: 8px;
        border-radius: 50%;
        border: none;
        background: rgba(255, 255, 255, 0.5);
        cursor: pointer;
        transition: background-color 0.2s;
    }
    
    .indicator.active {
        background: white;
    }
</style>
