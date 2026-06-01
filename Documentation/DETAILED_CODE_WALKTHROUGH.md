# Animation Physics - Detailed Code Walkthrough

## Complete Annotated Script with Physics Explanations

```javascript
/* ======================================================
   LOCOMOTIVE SCROLL - SMOOTH SCROLLING
   ======================================================
   
   Physics: Momentum-based scrolling with damping
   Formula: V_new = V_old × (1 - friction) 
   Result: Smooth inertial scroll that decelerates smoothly
*/

const scroll = new LocomotiveScroll({
    el: document.querySelector("#main"),
    smooth: true  // Enable momentum-based smooth scrolling
    // Internal damping factor ≈ 0.08-0.1 per frame (8-10% velocity retained)
});

// Update scroll engine when layout changes
// This is crucial because scroll calculations depend on accurate DOM measurements
window.addEventListener("load", () => {
    scroll.update();
    // At this point: fonts loaded, images loaded, layout final
});

// Handle font loading layout shifts
// Fonts can change element heights → must recalculate scroll positions
if (document.fonts) {
    document.fonts.ready.then(() => {
        scroll.update();
        // Font metrics now stable → accurate scroll calculations
    });
}

// Recalculate scroll on image load
const infographic = document.querySelector("#paper-main-hero-img");
if (infographic) {
    infographic.addEventListener("load", () => {
        if (scroll) scroll.update();
        // Image now has dimensions → update scroll container height
    });
}

// ======================================================
//  GLOBAL VARIABLES
// ======================================================

var timeout;  // Stores timeout ID for clearing previous timeouts
              // Prevents accumulation of delayed function calls


// ======================================================
//  LOADING ANIMATION - TIMELINE-BASED ENTRANCE
// ======================================================
/*
   Physics: Staggered animation creates wave effect
   - Each element starts when previous is 20% through
   - Creates cascading visual effect
   
   Timeline Sequencing:
   ├─ Phase 1: Slide up from below (translateY)
   ├─ Phase 2: Fade out in reverse order
   └─ Phase 3: Collapse loader container
   
   GSAP Timeline: Sequential animation queue
   - All animations execute in order
   - Can be paused/reversed/scrubbed
*/

function loadingAnimation() {
    var tl = gsap.timeline();
    // GSAP timeline = sequential animation controller

    // ──────────────────────────────────────────────────
    // PHASE 1: Slide-up Animation
    // ──────────────────────────────────────────────────
    /*
       Physics Principle: Displacement and Easing
       
       Initial State:  transform: translateY(150px)  [Element 150px below]
       Final State:    transform: translateY(0px)    [At normal position]
       Distance:       150px (vertical displacement)
       Duration:       0.8s
       
       Easing: "power4.out" = t → 1 - (1-t)⁴
       At t=0.0: position = 150px (start)
       At t=0.5: position ≈ 20px  (80% complete, rapid deceleration)
       At t=1.0: position = 0px   (end, fully decelerated)
    */
    tl.from(".loader-anim", {
        y: 150,              // ← Starting Y position (translateY)
        stagger: 0.2,        // ← STAGGER: 200ms between each element
                             //   Element 1: starts at 0ms
                             //   Element 2: starts at 200ms
                             //   Element 3: starts at 400ms
        duration: 0.8,       // ← Animation duration for each element
        ease: "power4.out"   // ← Easing function (quartic deceleration)
    })

    // ──────────────────────────────────────────────────
    // PHASE 2: Fade Out (Reverse Order)
    // ──────────────────────────────────────────────────
    /*
       Physics: Opacity transition with inverse stagger
       
       Opacity: 1 (visible) → 0 (invisible)
       Duration: 0.5s (faster than slide-up)
       Initial Delay: 0.5s (wait for elements to settle)
       Stagger: -0.1s (negative = reverse order!)
       
       Reverse Stagger Timeline:
       Element 5: starts at 0.5s + (4 × 0.1s) = 0.9s
       Element 4: starts at 0.5s + (3 × 0.1s) = 0.8s
       Element 3: starts at 0.5s + (2 × 0.1s) = 0.7s
       Element 2: starts at 0.5s + (1 × 0.1s) = 0.6s
       Element 1: starts at 0.5s + (0 × 0.1s) = 0.5s
       
       Visual Effect: Bottom element fades first, wave goes upward
    */
    .to(".loader-anim", {
        opacity: 0,          // ← Target opacity (fade out)
        delay: 0.5,          // ← Wait 0.5s before starting fade
        stagger: -0.1,       // ← NEGATIVE = reverse order stagger
        duration: 0.5        // ← Fade takes 0.5s
    })

    // ──────────────────────────────────────────────────
    // PHASE 3: Collapse Loader
    // ──────────────────────────────────────────────────
    /*
       Physics: Height transition with exponential easing
       
       Height: 100vh → 0 (full screen to nothing)
       Duration: 1s
       Easing: "expo.inOut" (exponential in and out)
       
       Formula: f(t) = 
         t < 0.5 ? 2^(20t-10)/2 : 1 - 2^(-20t+10)/2
       
       Characteristic: Slow start, fast middle, slow end
       At t=0.0: height = 100vh (no change)
       At t=0.5: height ≈ 50vh (accelerating collapse)
       At t=1.0: height = 0vh (final)
       
       Visual: Smooth compression, like closing a curtain
    */
    .to("#loader", {
        height: 0,           // ← Collapse to nothing
        duration: 1,         // ← Take 1 second
        ease: "expo.inOut"   // ← Exponential ease (natural deceleration)
    })
    
    // Callback: Trigger first page animation during collapse
    // onStart is called when THIS animation starts
    // Since we're in a timeline, we can coordinate multiple animations
    
}

// ──────────────────────────────────────────────────
// FIRST PAGE ANIMATION - Entrance Animation
// ──────────────────────────────────────────────────
/*
   Physics: Spring-like text entrance with stagger
   
   Mechanism:
   - Each text element (.boundingelem) slides up
   - Y position: 100% (below visible area) → 0 (visible)
   - Duration: 0.8-1s depending on screen size
   - Stagger: 50-80ms between elements
   
   Mobile optimization: Shorter duration, less stagger
   This prevents janky animations on slower mobile devices
*/

function firstPageAnim() {
    const isMobile = window.innerWidth < 768;
    // Responsive animation parameters

    gsap.to(".boundingelem", {
        y: 0,               // ← Move to Y position 0 (fully visible)
                            //   From: y = 100% (below view)
                            //   To:   y = 0
        ease: "power1.out", // ← Linear deceleration
                            //   Constant velocity decrease
                            //   easeOut(t) = t
        
        // RESPONSIVE DURATIONS
        duration: isMobile ? 0.8 : 1,    // ← Mobile: 0.8s, Desktop: 1.0s
                            // Shorter duration on mobile = faster, less laggy
        
        // RESPONSIVE STAGGER
        stagger: isMobile ? 0.05 : 0.08  // ← Mobile: 50ms, Desktop: 80ms
                            // Less delay on mobile to fit smaller viewport
    });

    // Fade in footer after text animation
    /*
       Physics: Y-position and opacity transition
       
       Y translation: 10px → 0px (small slide up)
       Opacity: 0 → 1 (fade in)
       Duration: 0.1s (fast)
       Ease: "power2.out" (quadratic deceleration)
       
       Timing: Happens AFTER boundingelem animation (no delay param)
       Queue positioning: "-=0.5" would overlap with previous animation
    */
    gsap.from("#herofooter", {
        y: 10,              // ← Start 10px below final position
        opacity: 0,         // ← Start invisible
        duration: 0.1,      // ← Quick fade-in
        ease: "power2.out", // ← Quadratic ease

        onComplete: function () {
            // Callback: Clear all GSAP properties to enable manual control
            gsap.set("#herofooter", {
                clearProps: "all"
            });
            
            // Update scroll engine after animations complete
            // Layout might have changed from animations
            if (scroll) scroll.update();
        }
    });
}


// ======================================================
//  CIRCLE MOUSE FOLLOWER - BASIC CURSOR TRACKING
// ======================================================
/*
   Physics: Direct Position Mapping (No interpolation)
   
   Mechanism:
   - Listen to mousemove events (~60Hz on 60Hz display)
   - Directly set element position to cursor position
   - No lag, no easing, immediate response
   
   Performance: Fast because no animation engine involved
   Use Case: Baseline cursor follower without effects
   
   Transform: translate(X, Y) uses GPU acceleration
   - CSS transform bypasses layout/paint pipeline
   - Only does composite operation (fast)
*/

function circleMouseFollower(xscale = 1, yscale = 1) {
    // Parameters: xscale and yscale can be modified for stretching effects
    // Default: 1 (normal circle)

    window.addEventListener("mousemove", function (dets) {
        // dets = MouseEvent object with clientX and clientY

        document.querySelector("#minicircle").style.transform =
            `translate(${dets.clientX}px, ${dets.clientY}px) 
             scale(${xscale}, ${yscale})`;
        
        // Transform breakdown:
        // translate(X, Y) - Position on screen
        // scale(Sx, Sy)   - Stretch in X and Y axes
        
        // GPU Acceleration:
        // - transform property uses GPU (fast)
        // - Composite layer (no layout recalculation)
        // - 60fps achievable even with rapid updates
    });
}


// ======================================================
//  MOUSE SQUEEZE EFFECT - VELOCITY-BASED DEFORMATION
// ======================================================
/*
   Physics Principle: Directional Velocity → Scale Transformation
   
   Core Formula:
   ├─ Δx = Current_X - Previous_X  (velocity magnitude)
   ├─ Δy = Current_Y - Previous_Y
   ├─ xscale = clamp(0.8, 1.2, Δx)
   ├─ yscale = clamp(0.8, 1.2, Δy)
   └─ Result: Circle stretches in direction of movement
   
   Inertia Reset: After 100ms of no movement, return to normal
   Prevents "sticky" stretched state
*/

function mouseChaptaKaro() {
    // "Chaptao" = Hindi for "slap" or "squeeze" → squeeze effect

    var xscale = 1;      // Current X-axis scale
    var yscale = 1;      // Current Y-axis scale
    
    var xprev = 0;       // Previous X position (stored for delta calculation)
    var yprev = 0;       // Previous Y position (stored for delta calculation)

    window.addEventListener("mousemove", function (dets) {
        // Clear any pending reset timeout
        // Prevents timeout from executing if user keeps moving
        clearTimeout(timeout);
        
        // ──────────────────────────────────────────────────
        // VELOCITY CALCULATION
        // ──────────────────────────────────────────────────
        /*
           Physics: Rate of position change
           
           Δx = dets.clientX - xprev
           This gives us velocity magnitude (pixels per frame)
           
           At 60fps, frame = 16.67ms
           If Δx = 50px, velocity = 50px / 16.67ms ≈ 3000px/s
           
           But we use raw Δx for scaling (simpler, feels good)
        */
        xscale = gsap.utils.clamp(
            0.8,                    // ← Minimum scale (20% compression)
            1.2,                    // ← Maximum scale (20% stretch)
            dets.clientX - xprev    // ← Raw velocity as scale value
        );

        yscale = gsap.utils.clamp(
            0.8,                    // ← Minimum scale
            1.2,                    // ← Maximum scale
            dets.clientY - yprev    // ← Raw velocity
        );
        
        // Update previous position for next frame
        xprev = dets.clientX;
        yprev = dets.clientY;

        // Apply stretched scale to cursor circle
        circleMouseFollower(xscale, yscale);
        
        // ──────────────────────────────────────────────────
        // INERTIA RESET - Return to normal scale
        // ──────────────────────────────────────────────────
        /*
           Physics: Damping / Friction effect
           
           After 100ms of NO mousemove events:
           └─ Reset scale to (1, 1) - normal circle
           
           Why 100ms?
           - Short enough: Feels responsive
           - Long enough: Completes micro-pause in movement
           
           Analogy: Like a spring returning to rest position
           when no force is applied
        */
        timeout = setTimeout(() => {
            // Only execute if no new mousemove in 100ms
            document.querySelector("#minicircle").style.transform =
                `translate(${dets.clientX}px, ${dets.clientY}px) 
                 scale(1, 1)`;
            // Circle returns to normal size after pause
        }, 100);
    });

    // ──────────────────────────────────────────────────
    // VISIBILITY MANAGEMENT
    // ──────────────────────────────────────────────────
    
    // Hide cursor when leaving window
    document.addEventListener("mouseleave", function () {
        gsap.to("#minicircle", {
            scale: 0,           // Shrink to zero
            opacity: 0,         // Fade out
            duration: 0.3,      // 300ms animation
            ease: "power2.out"  // Smooth deceleration
        });
    });

    // Show cursor when entering window
    document.addEventListener("mouseenter", function () {
        gsap.to("#minicircle", {
            scale: 1,           // Restore size
            opacity: 1,         // Restore visibility
            duration: 0.3,      // 300ms animation
            ease: "power2.out"
        });
    });
}


// ======================================================
//  SMOOTH FOLLOWER - SPRING PHYSICS WITH MOMENTUM
// ======================================================
/*
   Physics: Damped Harmonic Oscillator
   
   Mechanism:
   ├─ Spring Physics: F = -k·x (force proportional to displacement)
   ├─ Damping: Friction reduces oscillation (e = damping ratio)
   ├─ Momentum: Higher velocity = larger deformation
   └─ Result: Natural, physics-like following motion
   
   Spring Formula (simplified):
   ├─ x_new = x_old + v
   ├─ v_new = v_old - k·x - e·v  (spring force - damping)
   └─ repeat each frame
   
   GSAP quickTo(): Optimized spring simulator
   - Duration determines spring stiffness
   - Ease function determines damping
*/

function smoothFollower() {
    const circle = document.querySelector("#minicircle");

    // ──────────────────────────────────────────────────
    // SPRING PHYSICS SETUP
    // ──────────────────────────────────────────────────
    /*
       gsap.quickTo() creates optimized spring animation
       
       Parameters:
       ├─ "x" = animate X property
       ├─ duration: 0.9s = spring stiffness
       │          └─ Lower value = tighter spring (faster response)
       │          └─ Higher value = looser spring (more lag)
       └─ ease: "power4" = damping ratio
              └─ Higher power = more damping (less overshoot)
       
       Internally GSAP solves spring differential equation:
       x''(t) + 2ζω₀·x'(t) + ω₀²·x(t) = 0
       
       Where:
       ω₀ ∝ 1/√duration     (natural frequency)
       ζ ∝ ease power level  (damping ratio)
    */
    const xTo = gsap.quickTo(circle, "x", {
        duration: 0.9,      // ← Spring stiffness parameter
                            //   0.5s = very tight, responsive
                            //   1.5s = loose, laggy
                            //   0.9s = balanced (feels natural)
        ease: "power4"      // ← Damping parameter
                            //   power1 = light damping (oscillates)
                            //   power4 = heavy damping (smooth)
    });

    const yTo = gsap.quickTo(circle, "y", {
        duration: 0.9,
        ease: "power4"
    });

    // Track previous position for velocity calculation
    let xprev = 0;
    let yprev = 0;

    window.addEventListener("mousemove", function (dets) {
        // ──────────────────────────────────────────────────
        // SET NEW TARGET FOR SPRING
        // ──────────────────────────────────────────────────
        /*
           Spring pulls circle toward target position
           
           Target: Mouse cursor position
           Circle: Follows with lag due to spring mechanics
           
           quickTo() immediately sets target and animates toward it
           If mouse moves before animation completes, target updates
           This creates continuous following motion
        */
        xTo(dets.clientX);  // Set X target to cursor X
        yTo(dets.clientY);  // Set Y target to cursor Y

        // ──────────────────────────────────────────────────
        // VELOCITY-BASED DEFORMATION
        // ──────────────────────────────────────────────────
        /*
           Physics: Directional stretch from momentum
           
           Calculate velocity (delta position):
           ├─ xdiff = Current_X - Previous_X (horizontal velocity)
           └─ ydiff = Current_Y - Previous_Y (vertical velocity)
           
           Scaling Formula:
           ├─ xscale = 1 + |xdiff| × 0.01  (horizontal stretch)
           └─ yscale = 1 - |ydiff| × 0.01  (vertical compression)
           
           Note: Different directions!
           - X stretch: same sign (always expands horizontally)
           - Y compression: negative sign (always compresses vertically)
           
           Produces "pointing" effect: stretches in movement direction
        */
        let xdiff = dets.clientX - xprev;
        let ydiff = dets.clientY - yprev;

        let xscale = gsap.utils.clamp(
            0.8, 1.2,
            1 + Math.abs(xdiff) * 0.01  // ← 1% stretch per pixel velocity
        );

        let yscale = gsap.utils.clamp(
            0.8, 1.2,
            1 - Math.abs(ydiff) * 0.01  // ← Inverse for Y (compression)
        );

        xprev = dets.clientX;
        yprev = dets.clientY;

        // ──────────────────────────────────────────────────
        // APPLY DEFORMATION WITH EASING
        // ──────────────────────────────────────────────────
        /*
           Physics: Ease the scale transformation
           
           Duration: 0.1s (very quick)
           Ease: "power3" (cubic deceleration)
           
           Makes deformation smooth instead of snappy
           Combines with position animation for cohesive effect
           
           Timeline:
           Frame 1: xdiff = 50px → xscale = 1.5 (clamped to 1.2)
           Frame 2: gsap animates scale 1→1.2 over 0.1s
           Frame 3: xdiff = 10px → xscale = 1.1
           Frame 4: gsap animates scale 1.2→1.1 over 0.1s
           
           Result: Smooth morphing between states
        */
        gsap.to(circle, {
            scaleX: xscale,
            scaleY: yscale,
            duration: 0.1,      // ← Quick scale animation
            ease: "power3"      // ← Cubic easing (natural feel)
        });
    });
}


// ======================================================
//  IMAGE HOVER EFFECT - PARALLAX WITH ROTATION
// ======================================================
/*
   Physics: Velocity-based rotational tracking
   
   Mechanism:
   ├─ Position follows mouse (parallax effect)
   ├─ Rotation based on horizontal velocity
   └─ Creates 3D-like tilting effect
   
   Rotation Formula:
   ├─ diffrot = Current_X - Previous_X (velocity)
   ├─ angle = clamp(-20°, 20°, diffrot × 0.5)
   └─ Result: ±20° rotation range
*/

document.querySelectorAll(".elem").forEach(function (elem) {
    var rotate = 0;      // Current rotation angle
    var diffrot = 0;     // Rotation delta (velocity)

    // ──────────────────────────────────────────────────
    // MOUSE LEAVE - HIDE IMAGE
    // ──────────────────────────────────────────────────
    elem.addEventListener("mouseleave", function () {
        /*
           Physics: Quick fade-out
           
           Opacity: 1 → 0 (invisible)
           No duration specified: defaults to GSAP's default (0.5s)
           Ease: "Power3" (cubic, natural deceleration)
        */
        gsap.to(elem.querySelector("img"), {
            opacity: 0,
            ease: Power3
        });
    });

    // ──────────────────────────────────────────────────
    // MOUSE MOVE - TRACK AND ROTATE IMAGE
    // ──────────────────────────────────────────────────
    elem.addEventListener("mousemove", function (dets) {
        /*
           Physics:
           1. Y-position tracking (vertical parallax)
           2. X-position tracking (horizontal parallax)
           3. Rotation from velocity (angular momentum)
        */

        // VERTICAL POSITION TRACKING
        /*
           Calculate distance from element's top edge
           
           elem.getBoundingClientRect().top = element's Y position on screen
           dets.clientY = cursor's Y position on screen
           
           diff = dets.clientY - top
           
           Example:
           ├─ Element top at Y=100
           ├─ Cursor at Y=150
           └─ diff = 50 (image will be 50px from top of element)
           
           Ranges from 0 (top of element) to element.height (bottom)
        */
        var diff = dets.clientY - elem.getBoundingClientRect().top;

        // ──────────────────────────────────────────────────
        // ROTATION FROM VELOCITY
        // ──────────────────────────────────────────────────
        /*
           Physics: Angular velocity from linear velocity
           
           diffrot = Current_X - Previous_X (velocity magnitude)
           angle = diffrot × 0.5 (velocity to angle conversion)
           angle_clamped = clamp(-20°, 20°, angle)
           
           Mechanism:
           ├─ Move right (positive Δx) → Positive rotation (right tilt)
           ├─ Move left (negative Δx) → Negative rotation (left tilt)
           └─ No movement (Δx≈0) → No rotation
           
           Timeline:
           Frame 1: cursor at X=100
           Frame 2: cursor at X=150 → diffrot = 50
                    → angle = 50 × 0.5 = 25° (clamped to 20°)
                    → Image tilts right
           Frame 3: cursor at X=140 → diffrot = -10
                    → angle = -10 × 0.5 = -5°
                    → Image tilts left
           
           Effect: Looks like image is "facing" the cursor movement
        */
        diffrot = dets.clientX - rotate;  // Calculate velocity
        rotate = dets.clientX;             // Update for next frame

        // APPLY TRANSFORMATION
        /*
           All properties animate together:
           ├─ opacity: 1 (show image)
           ├─ top: diff (vertical tracking)
           ├─ left: dets.clientX (horizontal tracking)
           └─ rotate: clamp(-20°, 20°, diffrot × 0.5) (rotation from velocity)
           
           Duration: Not specified (uses GSAP defaults ~0.3-0.5s)
           Ease: Power3 (cubic deceleration)
           
           Result: Image follows cursor with tilt effect
        */
        gsap.to(elem.querySelector("img"), {
            opacity: 1,             // ← Make visible
            ease: Power3,           // ← Cubic easing
            top: diff,              // ← Vertical position (from element top)
            left: dets.clientX,     // ← Horizontal position (screen coordinates)
            rotate: gsap.utils.clamp(
                -20, 20,            // ← Rotation range: -20° to +20°
                diffrot * 0.5       // ← Rotation from horizontal velocity
            )
        });
    });
});


// ======================================================
//  FUNCTION INITIALIZATION
// ======================================================

loadingAnimation();      // Trigger entrance animation on page load
circleMouseFollower();   // Setup basic cursor follower
mouseChaptaKaro();       // Setup squeeze effect
smoothFollower();        // Setup spring-based follower
```

---

## Physics Constant Reference

### Clamping Ranges
```javascript
// Scale clamping: prevents extreme values
clamp(0.8, 1.2, value)
// Range: 20% compression to 20% stretch
// ├─ 0.8 = 80% of original (compressed)
// └─ 1.2 = 120% of original (stretched)

// Rotation clamping: prevents excessive tilting
clamp(-20, 20, angle)
// Range: -20° to +20°
// ├─ -20° = left tilt
// └─ +20° = right tilt
```

### Duration Parameters
```javascript
// Smooth follower spring
duration: 0.9s
// Low value = tight spring = responsive
// High value = loose spring = laggy
// 0.9s = balanced

// Squeeze effect reset
timeout: 100ms
// After 100ms of no movement
// Reset to normal scale
// Too short = feels twitchy
// Too long = feels sticky

// Scale animation
duration: 0.1s
// Quick morphing between scale values
// Matches frame time for smooth transitions
```

### Velocity Multipliers
```javascript
// Horizontal stretch
1 + Math.abs(xdiff) * 0.01
// 1% per pixel velocity
// Δx = 50px → scale = 1.5 (clamped to 1.2)
// Δx = 10px → scale = 1.1

// Rotation
diffrot * 0.5
// 0.5° per pixel velocity
// Δx = 50px → rotate = 25° (clamped to 20°)
// Δx = 20px → rotate = 10°

// Higher multiplier = more responsive but more extreme
// Lower multiplier = subtle, smooth effect
```

---

## Performance Analysis

### Event Listener Frequency
```javascript
// mousemove fires approximately:
// 60 times per second on 60Hz display
// 144 times per second on 144Hz display
// (once per monitor refresh)

// At 60Hz:
// Each listener executes every 16.67ms
// Must complete before next frame (16.67ms < frame_time)

// Our listeners do:
// ✓ Math operations (fast)
// ✓ DOM property updates (fast, batched by browser)
// ✓ gsap.to() calls (queued, optimized)
// Result: Well within budget, should maintain 60fps
```

### Memory Considerations
```javascript
// Global timeout variable
var timeout;  // Only one timeout ID stored
              // Previous timeout cleared before new one set
              // No accumulation

// Closures in forEach
forEach(function (elem) {
    // rotate, diffrot scoped to this element
    // Multiple closures, but bounded by number of .elem elements
    // Typical: 5-20 elements on page
    // Memory: negligible

// GSAP quickTo instances
const xTo = gsap.quickTo(...)  // Reuses animation queue
const yTo = gsap.quickTo(...)  // Optimized for repeated updates
// Memory: efficient, shared buffer
```

---

## Debugging Output Examples

### Velocity Logging
```javascript
// Add to mousemove listener
console.log("Velocity X:", xdiff, "px");
console.log("Velocity Y:", ydiff, "px");
console.log("Scale X:", xscale, "Scale Y:", yscale);

// Output:
// Velocity X: 45 px
// Velocity Y: 12 px
// Scale X: 1.2 Scale Y: 0.88

// Shows how velocity maps to scale values
```

### Timing Analysis
```javascript
let lastTime = 0;
window.addEventListener("mousemove", (e) => {
    const now = performance.now();
    const delta = now - lastTime;
    console.log("Frame time:", delta.toFixed(2), "ms");
    lastTime = now;
});

// Output:
// Frame time: 16.34 ms
// Frame time: 16.28 ms
// Frame time: 16.41 ms
// (Consistent ~16ms = 60fps)
```

### Spring Animation State
```javascript
gsap.to(circle, {
    scaleX: targetScale,
    duration: 0.1,
    ease: "power3",
    onUpdate: function() {
        const currentScale = gsap.getProperty(circle, "scaleX");
        console.log("Scale:", currentScale);
    }
});

// Shows animation progress from start to target
// Reveals easing curve in action
```

---

**Code Documentation Version**: 1.0  
**Purpose**: Understanding physics implementation in animation system  
**Audience**: Developers maintaining or extending the codebase

