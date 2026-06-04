// ======================================================
// CORE ENGINE INITIALIZATION
// ======================================================

let scrollContainer;

document.addEventListener("DOMContentLoaded", () => {
    const mainWrapper = document.querySelector("#main");
    
    // Fail-safe init if Locomotive Target exists on current page
    if (mainWrapper) {
        const reduceMotion = window.matchMedia("(prefers-reduced-motion: reduce)").matches;
        const isTouchDevice = window.matchMedia("(hover: none), (pointer: coarse)").matches;

        scrollContainer = new LocomotiveScroll({
            el: mainWrapper,
            smooth: !isTouchDevice && !reduceMotion
        });
    }

    // Initialize modular feature modules securely
    initLoaderAndEntrances();
    initOptimizedMouseFollower();
    initImageHoverMatrix();
});

// Force refresh computations across framework lifecycle hooks
window.addEventListener("load", () => {
    if (scrollContainer) scrollContainer.update();
});

if (document.fonts) {
    document.fonts.ready.then(() => {
        if (scrollContainer) scrollContainer.update();
    });
}

// Target internal asset structures dynamically
const paperHeroImg = document.querySelector("#paper-main-hero-img");
if (paperHeroImg) {
    paperHeroImg.addEventListener("load", () => {
        if (scrollContainer) scrollContainer.update();
    });
}


// ======================================================
// LIFECYCLE ANIMATION INTERFACES
// ======================================================

function initLoaderAndEntrances() {
    const loader = document.querySelector("#loader");
    const loaderAnims = document.querySelectorAll(".loader-anim");
    const isMobile = window.matchMedia("(max-width: 768px)").matches;
    const reduceMotion = window.matchMedia("(prefers-reduced-motion: reduce)").matches;

    // Fallback directly to page entrance if loader element isn't present on page
    if (!loader) {
        executeFirstPageEntrance();
        return;
    }

    if (reduceMotion) {
        gsap.set(loader, { display: "none" });
        executeFirstPageEntrance();
        return;
    }

    const tl = gsap.timeline();

    if (loaderAnims.length > 0) {
        tl.from(loaderAnims, {
            y: isMobile ? 70 : 150,
            stagger: isMobile ? 0.1 : 0.2,
            duration: isMobile ? 0.45 : 0.8,
            ease: "power4.out"
        })
        .to(loaderAnims, {
            opacity: 0,
            delay: isMobile ? 0.15 : 0.5,
            stagger: isMobile ? -0.04 : -0.1,
            duration: isMobile ? 0.25 : 0.5
        });
    }

    tl.to(loader, {
        height: 0,
        duration: isMobile ? 0.55 : 1,
        ease: "expo.inOut",
        onStart: () => {
            executeFirstPageEntrance();
        },
        onComplete: () => {
            gsap.set(loader, { display: "none" });
            if (scrollContainer) scrollContainer.update();
        }
    });
}

function executeFirstPageEntrance() {
    const boundingElements = document.querySelectorAll(".boundingelem");
    const heroFooter = document.querySelector("#herofooter");
    const isMobile = window.innerWidth < 768;

    if (boundingElements.length > 0) {
        gsap.to(boundingElements, {
            y: 0,
            ease: "power1.out",
            duration: isMobile ? 0.8 : 1,
            stagger: isMobile ? 0.05 : 0.08
        });
    }

    if (heroFooter) {
        gsap.from(heroFooter, {
            y: 10,
            opacity: 0,
            duration: 0.4,
            ease: "power2.out",
            onComplete: () => {
                gsap.set(heroFooter, { clearProps: "all" });
                if (scrollContainer) scrollContainer.update();
            }
        });
    }
}


// ======================================================
// UNIFIED OPTIMIZED MOUSE TRACKING ENGINE
// ======================================================

function initOptimizedMouseFollower() {
    const circle = document.querySelector("#minicircle");
    if (!circle) return;

    const isTouchDevice = window.matchMedia("(hover: none), (pointer: coarse)").matches;
    if (isTouchDevice) {
        circle.style.display = "none";
        return;
    }

    // Use GSAP highly optimized quickTo pipelines for hardware acceleration
    const xTo = gsap.quickTo(circle, "x", { duration: 0.3, ease: "power3.out" });
    const yTo = gsap.quickTo(circle, "y", { duration: 0.3, ease: "power3.out" });

    let xPrev = 0;
    let yPrev = 0;
    let squashTimeout;

    window.addEventListener("mousemove", (dets) => {
        // Run spatial positioning coordinates directly
        xTo(dets.clientX);
        yTo(dets.clientY);

        // Track precise displacement velocities to calculate context squash
        const xDiff = dets.clientX - xPrev;
        const yDiff = dets.clientY - yPrev;

        xPrev = dets.clientX;
        yPrev = dets.clientY;

        // Mathematical conversion mapping raw drag speed to physical distortion constraints
        const speed = Math.sqrt(xDiff * xDiff + yDiff * yDiff);
        const clampScaleX = gsap.utils.clamp(0.8, 1.2, 1 + speed * 0.005);
        const clampScaleY = gsap.utils.clamp(0.8, 1.2, 1 - speed * 0.005);

        gsap.to(circle, {
            scaleX: clampScaleX,
            scaleY: clampScaleY,
            duration: 0.1,
            ease: "power2.out"
        });

        // Snap scaling structural defaults back to pure uniform shape when movement ceases
        clearTimeout(squashTimeout);
        squashTimeout = setTimeout(() => {
            gsap.to(circle, {
                scaleX: 1,
                scaleY: 1,
                duration: 0.2,
                ease: "power2.out"
            });
        }, 60);
    });

    // Handle viewport visibility states smoothly
    document.addEventListener("mouseleave", () => {
        gsap.to(circle, { scale: 0, opacity: 0, duration: 0.3, ease: "power2.out" });
    });

    document.addEventListener("mouseenter", () => {
        gsap.to(circle, { scale: 1, opacity: 1, duration: 0.3, ease: "power2.out" });
    });
}


// ======================================================
// IMAGE HOVER INLINE DISPLACEMENT MATRIX
// ======================================================

function initImageHoverMatrix() {
    const hoverElements = document.querySelectorAll(".elem");
    if (hoverElements.length === 0) return;

    const isTouchDevice = window.matchMedia("(hover: none), (pointer: coarse)").matches;
    if (isTouchDevice) return;

    hoverElements.forEach((elem) => {
        const targetImg = elem.querySelector("img");
        if (!targetImg) return;

        let lastX = 0;
        let rotationDelta = 0;

        elem.addEventListener("mouseleave", () => {
            gsap.to(targetImg, {
                opacity: 0,
                duration: 0.4,
                ease: "power3.out"
            });
        });

        elem.addEventListener("mousemove", (dets) => {
            // Compute vertical context distance from parent container card surface boundary line
            const internalTopOffset = dets.clientY - elem.getBoundingClientRect().top;
            
            rotationDelta = dets.clientX - lastX;
            lastX = dets.clientX;

            gsap.to(targetImg, {
                opacity: 1,
                duration: 0.4,
                ease: "power3.out",
                top: internalTopOffset,
                left: dets.clientX,
                // Clamps rotation values neatly between -15 and 15 degrees max
                rotation: gsap.utils.clamp(-15, 15, rotationDelta * 0.6)
            });
        });
    });
}

/* =========================================
   LET'S TALK BUTTON
========================================= */
function initMagneticButtons() {
    const magneticButtons = document.querySelectorAll(".magnetic-button");
    
    if (magneticButtons.length === 0) return;

    magneticButtons.forEach((btn) => {
        const text = btn.querySelector(".magnetic-text");

        btn.addEventListener("mousemove", (e) => {
            // Get boundaries of the button element relative to viewport
            const bound = btn.getBoundingClientRect();
            
            // Calculate the mouse position relative to the center of the button
            const x = e.clientX - bound.left - bound.width / 2;
            const y = e.clientY - bound.top - bound.height / 2;

            // Pull the outer button container toward the cursor (strength: 0.35)
            gsap.to(btn, {
                x: x * 0.35,
                y: y * 0.35,
                duration: 0.3,
                ease: "power2.out"
            });

            // Pull the inner text slightly less to create a parallax depth effect (strength: 0.2)
            if (text) {
                gsap.to(text, {
                    x: x * 0.2,
                    y: y * 0.2,
                    duration: 0.3,
                    ease: "power2.out"
                });
            }
        });

        // Snap both elements seamlessly back to center position when mouse leaves boundary
        btn.addEventListener("mouseleave", () => {
            gsap.to(btn, {
                x: 0,
                y: 0,
                duration: 0.5,
                ease: "elastic.out(1, 0.3)" // Gives it that organic, satisfying snap-back bounce
            });

            if (text) {
                gsap.to(text, {
                    x: 0,
                    y: 0,
                    duration: 0.5,
                    ease: "elastic.out(1, 0.3)"
                });
            }
        });
    });
}

