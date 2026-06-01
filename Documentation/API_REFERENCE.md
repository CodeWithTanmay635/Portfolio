# API Reference

Complete documentation of all animation functions, parameters, and their physics implementations.

---

## Table of Contents

- [Initialization](#initialization)
- [Locomotive Scroll](#locomotive-scroll)
- [Loading Animation](#loading-animation)
- [Mouse Cursor Effects](#mouse-cursor-effects)
  - [Circle Mouse Follower](#circle-mouse-follower)
  - [Mouse Squeeze Effect](#mouse-squeeze-effect)
  - [Smooth Follower](#smooth-follower)
- [Image Hover Effect](#image-hover-effect)
- [Advanced Configuration](#advanced-configuration)

---

## Initialization

### Overview
Initialize the animation system with Locomotive Scroll and all cursor effects.

### Basic Initialization

```javascript
// Step 1: Create Locomotive Scroll instance
const scroll = new LocomotiveScroll({
    el: document.querySelector("#main"),
    smooth: true
});

// Step 2: Update on page load
window.addEventListener("load", () => {
    scroll.update();
});

// Step 3: Initialize animations
loadingAnimation();
circleMouseFollower();
mouseChaptaKaro();
smoothFollower();
```

### Initialization Order

```
Locomotive Scroll
    ↓
loadingAnimation()      [Entrance animation]
    ↓
circleMouseFollower()   [Base cursor]
    ↓
mouseChaptaKaro()       [Squeeze effect]
    ↓
smoothFollower()        [Spring physics]
```

### Important Notes

- **Order matters**: Initialize Locomotive Scroll before cursor effects
- **DOM readiness**: Call functions after DOM is fully loaded
- **Mobile check**: Wrap expensive functions in device detection

---

## Locomotive Scroll

### `new LocomotiveScroll(options)`

Initializes smooth scrolling with momentum and parallax effects.

#### Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `el` | HTMLElement | Required | Main scroll container (usually `#main`) |
| `smooth` | Boolean | `true` | Enable smooth scrolling with momentum |
| `inertia` | Number | 0.1 | Damping factor (0-1, lower = more damping) |
| `class` | String | `"is-inview"` | Class added to visible elements |

#### Example

```javascript
const scroll = new LocomotiveScroll({
    el: document.querySelector("#main"),
    smooth: true,
    inertia: 0.08  // Faster deceleration
});
```

### `scroll.update()`

Recalculate scroll container dimensions and element positions.

**Must call after:**
- Page load
- Font loading completion
- Image loading
- Window resize
- DOM changes

#### Example

```javascript
window.addEventListener("load", () => {
    scroll.update();
});

document.fonts.ready.then(() => {
    scroll.update();
});

imgElement.addEventListener("load", () => {
    scroll.update();
});
```

### Physics Parameters

```
Momentum Formula:
V_new = V_old × (1 - inertia)

inertia = 0.1  (10% velocity retained per frame)
└─ Decelerates quickly, snappy feel

inertia = 0.05 (5% velocity retained per frame)
└─ Decelerates very quickly, choppy

inertia = 0.15 (15% velocity retained per frame)
└─ Slow deceleration, drift feel
```

---

## Loading Animation

### `loadingAnimation()`

Plays entrance animation sequence: slide-up → fade-out → collapse.

#### Signature

```javascript
function loadingAnimation() {
    var tl = gsap.timeline();
    // Animation sequence...
}
```

#### Animation Sequence

| Phase | Element | Action | Duration | Ease |
|-------|---------|--------|----------|------|
| 1 | `.loader-anim` | Slide up from y:150px → 0 | 0.8s | power4.out |
| 2 | `.loader-anim` | Fade opacity 1 → 0 | 0.5s | (default) |
| 3 | `#loader` | Collapse height 100vh → 0 | 1s | expo.inOut |

#### Parameters (Customizable)

```javascript
// In loadingAnimation() function, modify these:
tl.from(".loader-anim", {
    y: 150,              // Starting Y displacement (pixels)
    stagger: 0.2,        // Delay between elements (seconds)
    duration: 0.8,       // Animation duration (seconds)
    ease: "power4.out"   // Easing function
})
```

#### Parameter Reference

| Parameter | Type | Default | Range | Notes |
|-----------|------|---------|-------|-------|
| `y` | Number | 150 | 0-500 | Starting Y offset in pixels |
| `stagger` | Number | 0.2 | 0.05-0.5 | Delay between elements (seconds) |
| `duration` | Number | 0.8 | 0.3-2 | Animation duration (seconds) |
| `ease` | String | power4.out | GSAP easings | See easing reference |

#### Example: Customize Duration

```javascript
// Slower, more dramatic entrance
function loadingAnimation() {
    var tl = gsap.timeline();
    
    tl.from(".loader-anim", {
        y: 150,
        stagger: 0.3,      // Slower stagger
        duration: 1.2,     // Longer animation
        ease: "power4.out"
    })
    .to(".loader-anim", {
        opacity: 0,
        duration: 0.8      // Longer fade
    })
    .to("#loader", {
        height: 0,
        duration: 1.5,     // Longer collapse
        ease: "expo.inOut"
    });
}
```

#### Example: Mobile Optimization

```javascript
function loadingAnimation() {
    const isMobile = window.innerWidth < 768;
    var tl = gsap.timeline();
    
    tl.from(".loader-anim", {
        y: 150,
        stagger: isMobile ? 0.1 : 0.2,    // Faster on mobile
        duration: isMobile ? 0.6 : 0.8,   // Shorter on mobile
        ease: "power4.out"
    })
    // ... rest of animation
}
```

#### Timeline Visualization

```
Elements: [1]  [2]  [3]  [4]  [5]

Phase 1 (Slide):
     [====]
        [====]
           [====]
              [====]
                 [====]

Phase 2 (Fade):
              [===]
                 [===]
                    [===]
                       [===]
                          [===]

Phase 3 (Collapse):
                       [========]
```

---

## Mouse Cursor Effects

### Circle Mouse Follower

#### `circleMouseFollower(xscale = 1, yscale = 1)`

Creates a basic cursor that directly follows mouse movement.

#### Signature

```javascript
function circleMouseFollower(xscale = 1, yscale = 1) {
    window.addEventListener("mousemove", function (dets) {
        // Position and scale cursor
    });
}
```

#### Parameters

| Parameter | Type | Default | Range | Description |
|-----------|------|---------|-------|-------------|
| `xscale` | Number | 1 | 0.5-2 | Horizontal scale (1 = normal) |
| `yscale` | Number | 1 | 0.5-2 | Vertical scale (1 = normal) |

#### Behavior

- **Direct mapping**: Cursor position = mouse position (no lag)
- **GPU accelerated**: Uses `transform: translate()`
- **60 FPS**: Updates on every mousemove event

#### Example

```javascript
// Basic usage
circleMouseFollower();

// With custom scale
circleMouseFollower(1.2, 0.8);  // Wide and compressed
```

#### Physics

```
Direct Position Mapping:
cursor_x = mouse_x
cursor_y = mouse_y

Scale Application:
scale: scale(xscale, yscale)

No lag, instant response
```

---

### Mouse Squeeze Effect

#### `mouseChaptaKaro()`

Creates velocity-based cursor deformation with timeout reset.

#### Signature

```javascript
function mouseChaptaKaro() {
    var xscale = 1;
    var yscale = 1;
    var xprev = 0;
    var yprev = 0;
    
    window.addEventListener("mousemove", function (dets) {
        // Calculate velocity and deform cursor
    });
}
```

#### Parameters (Customizable)

```javascript
// Inside mouseChaptaKaro():
xscale = gsap.utils.clamp(0.8, 1.2, dets.clientX - xprev);
//                         ↑    ↑
//                    min, max (customize these)

timeout = setTimeout(() => {
    // Reset to normal scale
}, 100);  // Reset delay in ms (customize this)
```

#### Parameter Reference

| Parameter | Type | Default | Range | Notes |
|-----------|------|---------|-------|-------|
| `min scale` | Number | 0.8 | 0.5-1 | Compression limit |
| `max scale` | Number | 1.2 | 1-2 | Stretch limit |
| `timeout` | Number | 100 | 50-500 | Reset delay (ms) |

#### Behavior

| Event | Behavior |
|-------|----------|
| Fast movement | Cursor stretches/compresses |
| Slow movement | Minimal deformation |
| No movement (100ms) | Return to normal scale |
| Leave window | Scale 0, fade out |
| Enter window | Scale 1, fade in |

#### Example: Extreme Squeeze

```javascript
// In mouseChaptaKaro():
xscale = gsap.utils.clamp(0.7, 1.3, dets.clientX - xprev);
//                         ↑    ↑
//                    More extreme range (30% stretch)
```

#### Physics Formula

```
Velocity Calculation:
Δx = Current_X - Previous_X

Scale Mapping:
scale = clamp(min, max, velocity)

Examples:
Δx = 50px  →  scale = 1.2 (maximum stretch)
Δx = 20px  →  scale = 1.0 (normal)
Δx = -30px →  scale = 0.8 (compression)

Reset (inertia):
After 100ms of no movement → scale returns to 1.0
```

---

### Smooth Follower

#### `smoothFollower()`

Implements spring physics with velocity-based deformation.

#### Signature

```javascript
function smoothFollower() {
    const circle = document.querySelector("#minicircle");
    
    const xTo = gsap.quickTo(circle, "x", {
        duration: 0.9,
        ease: "power4"
    });
    
    const yTo = gsap.quickTo(circle, "y", {
        duration: 0.9,
        ease: "power4"
    });
    
    // ... velocity tracking and deformation
}
```

#### Parameters (Customizable)

```javascript
// Spring stiffness:
const xTo = gsap.quickTo(circle, "x", {
    duration: 0.9,    // Customize: 0.3-1.5
    ease: "power4"    // Customize: power1-power4
});

// Deformation sensitivity:
let xscale = gsap.utils.clamp(
    0.8, 1.2,
    1 + Math.abs(xdiff) * 0.01  // 0.01 = sensitivity
);
```

#### Parameter Reference

| Parameter | Type | Default | Range | Effect |
|-----------|------|---------|-------|--------|
| `duration` | Number | 0.9 | 0.3-1.5 | Spring stiffness (lower = tighter) |
| `ease` | String | power4 | power1-4 | Damping (higher = less overshoot) |
| `sensitivity` | Number | 0.01 | 0.005-0.05 | Deformation per pixel velocity |
| `scale clamp` | Array | [0.8, 1.2] | Any range | Scale min/max limits |

#### Behavior

| Condition | Behavior |
|-----------|----------|
| Cursor moves | Circle follows with ~0.9s lag |
| Fast horizontal movement | X stretches (up to 1.2x) |
| Fast vertical movement | Y compresses (down to 0.8x) |
| Mixed movement | Combined stretch/compress |
| Movement stops | Smooth deformation fade |

#### Example: Tight Spring

```javascript
const xTo = gsap.quickTo(circle, "x", {
    duration: 0.5,      // Very tight spring
    ease: "power4"
});

const yTo = gsap.quickTo(circle, "y", {
    duration: 0.5,
    ease: "power4"
});
```

#### Example: Loose Spring

```javascript
const xTo = gsap.quickTo(circle, "x", {
    duration: 1.5,      // Loose, laggy spring
    ease: "power2"      // Less damping, more wiggle
});
```

#### Physics Formula

```
Spring Dynamics:
F = -k·x - c·v
x'' + 2ζω₀x' + ω₀²x = 0

Where:
k ∝ 1/duration     (spring constant)
c ∝ ease power     (damping coefficient)
ζ = damping ratio
ω₀ = natural frequency

GSAP quickTo() solves this equation internally
Result: Natural, oscillating-free motion

Deformation:
xscale = 1 + |velocity_x| × sensitivity
yscale = 1 - |velocity_y| × sensitivity

Clamping prevents extreme values:
scale ∈ [0.8, 1.2]
```

---

## Image Hover Effect

### `addEventListener("mousemove", imageHoverHandler)`

Creates parallax image following with velocity-based rotation.

#### Signature

```javascript
document.querySelectorAll(".elem").forEach(function (elem) {
    var rotate = 0;
    var diffrot = 0;
    
    elem.addEventListener("mousemove", function (dets) {
        // Track image position and rotation
    });
});
```

#### Parameters (Customizable)

```javascript
// In the mousemove handler:
var diff = dets.clientY - elem.getBoundingClientRect().top;
//          └─ Vertical position tracking

diffrot = dets.clientX - rotate;  // Angular velocity
rotate = dets.clientX;

gsap.to(elem.querySelector("img"), {
    opacity: 1,
    top: diff,
    left: dets.clientX,
    rotate: gsap.utils.clamp(-20, 20, diffrot * 0.5)
    //                        ↑   ↑
    //                   Rotation range (degrees)
});
```

#### Parameter Reference

| Parameter | Type | Default | Range | Notes |
|-----------|------|---------|-------|-------|
| `min rotation` | Number | -20 | -90 to 0 | Left tilt limit (degrees) |
| `max rotation` | Number | 20 | 0 to 90 | Right tilt limit (degrees) |
| `velocity multiplier` | Number | 0.5 | 0.1-2 | Sensitivity to movement |

#### Behavior

| Movement | Image Behavior |
|----------|----------------|
| Cursor enters element | Opacity 0 → 1 (fade in) |
| Cursor moves right | Image follows + tilts right |
| Cursor moves left | Image follows + tilts left |
| Cursor moves down | Image follows vertically |
| Cursor moves up | Image follows vertically |
| Cursor leaves element | Opacity 1 → 0 (fade out) |

#### Example: More Extreme Rotation

```javascript
// Increase rotation range
rotate: gsap.utils.clamp(-45, 45, diffrot * 0.5)
//                       ↑   ↑
//                  ±45° rotation range
```

#### Example: Higher Velocity Sensitivity

```javascript
// More responsive to movement speed
rotate: gsap.utils.clamp(-20, 20, diffrot * 1.0)
//                                      ↑
//                               2x sensitivity
```

#### Physics Formula

```
Vertical Position:
top = cursor_y - element_top
Range: 0 to element.height

Horizontal Position:
left = cursor_x
Range: full screen width

Rotational Velocity:
ω = diffrot * multiplier
ω = (current_x - previous_x) * 0.5

Rotation Range:
angle ∈ [-20°, 20°] (clamped)

Examples:
Δx = 40px  →  angle = 20° (right tilt, clamped)
Δx = 20px  →  angle = 10° (slight right tilt)
Δx = -20px →  angle = -10° (slight left tilt)
```

---

## Advanced Configuration

### Custom Easing Functions

GSAP supports various easing functions:

#### Recommended Easings

```javascript
// Linear easing (constant deceleration)
ease: "power1.out"

// Quadratic (medium deceleration)
ease: "power2.out"

// Cubic (common, natural feel)
ease: "power3.out"

// Quartic (rapid deceleration, responsive)
ease: "power4.out"

// Exponential (slow start, fast end)
ease: "expo.inOut"

// Back (slight overshoot)
ease: "back.out"

// Elastic (spring-like bounce)
ease: "elastic.out"

// Custom cubic-bezier
ease: "cubic-bezier(0.25, 0.46, 0.45, 0.94)"
```

### Performance Optimization

#### Mobile Detection

```javascript
const isMobile = window.innerWidth < 768;
const isTablet = window.innerWidth >= 768 && window.innerWidth < 1024;
const isDesktop = window.innerWidth >= 1024;

if (isDesktop) {
    smoothFollower();       // Heavy animations
    mouseChaptaKaro();
} else if (isTablet) {
    circleMouseFollower();  // Medium animations
} else {
    // Mobile: minimal animations
}
```

#### Disable on Slow Devices

```javascript
// Check for reduced motion preference
const prefersReducedMotion = window.matchMedia(
    "(prefers-reduced-motion: reduce)"
).matches;

if (!prefersReducedMotion) {
    loadingAnimation();
    smoothFollower();
}
```

### Scroll Events

```javascript
// Update scroll on custom events
window.addEventListener("resize", () => {
    scroll.update();
});

// Listen to scroll progress
scroll.on("scroll", (instance) => {
    console.log("Scroll progress:", instance.progress);
});

// Detect element in viewport
scroll.on("call", (value) => {
    console.log("Element visible:", value);
});
```

### Debug & Monitoring

```javascript
// Log mouse velocity
let lastX = 0, lastY = 0;
window.addEventListener("mousemove", (e) => {
    const vx = Math.abs(e.clientX - lastX);
    const vy = Math.abs(e.clientY - lastY);
    console.log(`Velocity: (${vx}, ${vy})`);
    lastX = e.clientX;
    lastY = e.clientY;
});

// Monitor animation performance
gsap.to(element, {
    x: 100,
    onUpdate: function() {
        console.log("Current progress:", this.progress());
    }
});

// Check FPS
let lastTime = performance.now();
let frameCount = 0;

function measureFPS() {
    frameCount++;
    const now = performance.now();
    if (now - lastTime >= 1000) {
        console.log(`FPS: ${frameCount}`);
        frameCount = 0;
        lastTime = now;
    }
    requestAnimationFrame(measureFPS);
}

measureFPS();
```

---

## Common Patterns

### Pattern: Fade in on Scroll

```javascript
gsap.from(".element", {
    scrollTrigger: {
        trigger: ".element",
        scroller: "[data-scroll-container]"
    },
    opacity: 0,
    y: 50,
    duration: 1
});
```

### Pattern: Parallax Image

```javascript
gsap.to(".parallax-image", {
    scrollTrigger: {
        trigger: ".parallax-image",
        scroller: "[data-scroll-container]",
        markers: false
    },
    y: -100,
    ease: "none"
});
```

### Pattern: Timeline Sequencing

```javascript
const tl = gsap.timeline();

tl.to(".element1", { duration: 1, opacity: 1 })
  .to(".element2", { duration: 1, opacity: 1 }, "-=0.5")
  .to(".element3", { duration: 1, opacity: 1 }, "-=0.5");
```

---

## Troubleshooting

### Animation Not Playing

```javascript
// Make sure DOM elements exist
console.assert(
    document.querySelector("#minicircle") !== null,
    "Cursor element not found"
);

// Check GSAP is loaded
console.assert(
    typeof gsap !== "undefined",
    "GSAP library not loaded"
);

// Verify functions are called
console.log("loadingAnimation called");
loadingAnimation();
```

### Poor Performance

```javascript
// Check FPS
// Open DevTools → Performance tab
// Record 5 seconds of interaction
// Look for consistent 60 FPS

// Check for layout thrashing
// DevTools → Console → Warnings about layout recalculation

// Reduce animation complexity on mobile
if (isMobile) {
    // Use simpler animations
}
```

### Scroll Not Working

```javascript
// Verify container is correct
const scroll = new LocomotiveScroll({
    el: document.querySelector("#main"),  // Must match HTML
    smooth: true
});

// Update after content loads
window.addEventListener("load", () => {
    scroll.update();
});
```

---

**Last Updated**: January 2024  
**Version**: 1.0  
**GSAP**: 3.13.0  
**Locomotive Scroll**: 3.5.4
