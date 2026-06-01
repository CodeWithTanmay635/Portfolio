# Animation Physics System - Quick Reference Guide

## System Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                  ANIMATION SYSTEM PIPELINE                   │
└─────────────────────────────────────────────────────────────┘

                          PAGE LOAD
                             ↓
                    [loadingAnimation()]
                      ├─ Staggered slide-up
                      ├─ Fade-out sequence
                      └─ Collapse loader
                             ↓
                    [firstPageAnim()]
                      ├─ Bounce text elements
                      └─ Fade footer in
                             ↓
                    [Locomotive Scroll Start]
                      └─ Smooth parallax scrolling
                             ↓
              USER INTERACTION (Mouse Moves)
                             ↓
        ┌───────────────────┬──────────────────┬──────────────┐
        ↓                   ↓                  ↓              ↓
    [Circle          [Mouse Squeeze]    [Smooth Follower]  [Image Hover]
    Follower]        • Velocity calc    • Spring physics   • Rotation
    • Instant        • Scale clamp      • Momentum         • Position
      update         • Reset timeout    • Stretch effect    tracking
        ↓                   ↓                  ↓              ↓
    Update DOM → GPU Acceleration → 60 FPS Rendering
```

---

## Physics Concept Quick Maps

### 1. VELOCITY CALCULATION

```
Previous Position: (100, 50)
Current Position:  (150, 70)
                    │    │
                    │    └─→ Δy = 20px
                    └───────→ Δx = 50px

Velocity Vector: [50px/frame, 20px/frame]
Speed: √(50² + 20²) ≈ 53.85 px/frame
```

**In Code:**
```javascript
xdiff = dets.clientX - xprev;  // 50
ydiff = dets.clientY - yprev;  // 20
```

---

### 2. CLAMPING (Value Limiting)

```
Without Clamping:              With Clamping:
Input ─────────────────────→  Input ──┐
                                      ├─→ clamp(0.8, 1.2, val)
       Range: [-∞, +∞]               ↓
       Uncontrolled values     Output ──┐
                                        ├─→ Range: [0.8, 1.2]
                                        ↓
                                   Controlled values

Example:
Input: 2.5  → clamp(0.8, 1.2, 2.5) = 1.2 (capped)
Input: 0.5  → clamp(0.8, 1.2, 0.5) = 0.8 (floored)
Input: 1.0  → clamp(0.8, 1.2, 1.0) = 1.0 (pass-through)
```

---

### 3. SPRING PHYSICS

```
Equilibrium Position
        ↓
┌───────●───────┐
│       │       │
│    Spring     │
│       │       │
└───────┴───────┘
    Target X

When displaced:
    ┌─────●────┐
    │    /|\   │  Force = -k × displacement
    │   / │ \  │  
    └──┴──┴───┘  
   Pulls back with strength proportional to distance

GSAP Implementation:
duration: 0.9s  →  Spring stiffness (lower = tighter spring)
ease: "power4"  →  Damping (prevents oscillation)

Position over time:
    ↗─────┐
    │      └────┐
    │            └──
    │━━━━━━━━━━━━━━ Target position
    0      0.9s     (settles at target)
```

---

### 4. EASING FUNCTIONS

```
POWER1.OUT (Linear Deceleration)
  Position
    │     ╱
    │    ╱
    │   ╱
    │  ╱
    │ ╱
    └─────────── Time
    Constant deceleration

POWER4.OUT (Quartic Deceleration)
  Position
    │     ╱╱╱
    │    ╱
    │   ╱
    │  ╱
    │ ╱
    └─────────── Time
    Fast start, slow finish (responsive)

EXPO.INOUT (Exponential)
  Position
    │      ╱─────
    │    ╱╱
    │   ╱
    │  ╱
    │╱
    └─────────── Time
    Accelerate then decelerate
```

---

## Physics Parameter Cheat Sheet

### Cursor Squeeze Effect
```
┌──────────────────────────────────────┐
│ MOVEMENT RIGHT (xdiff > 0)           │
├──────────────────────────────────────┤
│ xscale = clamp(0.8, 1.2, xdiff)     │
│                                      │
│ Δx = 50px  →  xscale = 1.2 (wide)   │
│ Δx = 10px  →  xscale = 1.0 (normal) │
│ Δx = -50px →  xscale = 0.8 (narrow) │
└──────────────────────────────────────┘

Timeline:
└─ Mousemove detected
   └─ Calculate velocity
      └─ Apply scale
         └─ Render
            └─ [100ms timeout] 
               └─ Reset to scale(1,1)
```

### Smooth Follower Stretch
```
Horizontal Movement:       Vertical Movement:
Right/Left motion         Up/Down motion
     ↔                         ↕
     
xscale = 1 + |Δx|×0.01   yscale = 1 - |Δy|×0.01
     
Δx = 50px                 Δy = 50px
xscale = 1.5→1.2          yscale = 0.5→0.8
(stretched)               (compressed)

Visual Result:
      ╱ ╲                    │
     │   │                   │
     │   │   →    ╱     ╲   │
     │   │              ╰───┘
      ╲ ╱
 (normal)          (stretched x)        (compressed y)
```

---

## Animation Timing Reference

### Loading Animation Stagger Pattern
```
Timeline with 5 loader elements:

Element 1: |====|
Element 2:    |====|
Element 3:       |====|
Element 4:          |====|
Element 5:             |====|
          0   0.2  0.4  0.6  0.8  1.0 (seconds)

Stagger = 0.2s (200ms between each)
Wave effect created from sequential timing
```

### Smooth Follower Response
```
Cursor Movement:
Time:  0ms    100ms   200ms   300ms   400ms   500ms
Pos:   (100,100)
        ├─────(150,120)─────(200,140)─────(250,160)
        │
Circle Pos:
        100   110     120     130     140     150 (follows smoothly)
        │────────────────────────────────────────→
        
Duration: 0.9s per target position
Result: Smooth "following" effect that lags behind cursor
```

---

## Common Physics Mistakes & Fixes

### ❌ Problem 1: Cursor Not Following Smoothly
```javascript
// WRONG: Using static position
circle.style.x = clientX;

// RIGHT: Using spring physics
gsap.quickTo(circle, "x", { duration: 0.9, ease: "power4" })(clientX);
```

### ❌ Problem 2: Uncontrolled Scale Values
```javascript
// WRONG: Scale can be 0 to infinity
scale = 1 + clientX * 0.1;  // Extreme values!

// RIGHT: Clamped to reasonable range
scale = clamp(0.8, 1.2, 1 + clientX * 0.01);
```

### ❌ Problem 3: Memory Leak from Timeouts
```javascript
// WRONG: Timeouts accumulate
addEventListener("mousemove", () => {
    setTimeout(() => { ... }, 100);  // New timeout every move
});

// RIGHT: Clear before setting new
addEventListener("mousemove", () => {
    clearTimeout(timeout);           // Clear old one
    timeout = setTimeout(() => { ... }, 100);
});
```

### ❌ Problem 4: Layout Thrashing
```javascript
// WRONG: Reading then writing causes reflow
for (let i = 0; i < 100; i++) {
    elem.style.left = elem.offsetLeft + 10;  // Read, write, read, write...
}

// RIGHT: Batch read, then batch write
const positions = [];
for (let i = 0; i < 100; i++) {
    positions.push(elem.offsetLeft);  // All reads
}
for (let i = 0; i < 100; i++) {
    elem.style.left = positions[i] + 10;  // All writes
}
```

### ❌ Problem 5: Poor Performance on Mobile
```javascript
// WRONG: Same intensive animations on all devices
smoothFollower();
mouseChaptaKaro();

// RIGHT: Feature detect and adapt
if (window.innerWidth >= 1024) {
    smoothFollower();
    mouseChaptaKaro();
} else {
    // Simpler animations for mobile
    circleMouseFollower();
}
```

---

## Performance Metrics

### Target Performance
```
Metric              | Target    | Your System
─────────────────────────────────────────────
Frame Rate          | 60 FPS    | ?
Frame Time          | 16.67 ms  | ?
Mousemove Rate      | ~60 Hz    | ?
Paint Time          | < 16 ms   | ?
Layout Shift        | 0         | ?

How to measure in Chrome DevTools:
1. Open Performance tab
2. Start recording
3. Move cursor across page
4. Stop recording
5. Look for consistent 60 FPS timeline
```

### Optimization Checklist
```
☐ Using transform: translate() [GPU]
☐ Using will-change: transform
☐ No reflows (reading DOM properties)
☐ Debouncing event listeners
☐ Clearing old timeouts
☐ Using GSAP quickTo (optimized)
☐ Hardware acceleration enabled
☐ No layout thrashing
```

---

## Velocity Scaling Reference

### Different Sensitivity Values

```
Scaling Formula: scale = 1 + |Δposition| × Sensitivity

Sensitivity = 0.01:
Δx = 10px  →  scale = 1.1
Δx = 50px  →  scale = 1.5 (clamped to 1.2)
Good for: Subtle, smooth effects

Sensitivity = 0.05:
Δx = 10px  →  scale = 1.5 (clamped)
Δx = 50px  →  scale = 3.5 (clamped)
Good for: Dramatic, responsive effects

Sensitivity = 0.005:
Δx = 10px  →  scale = 1.05
Δx = 50px  →  scale = 1.25
Good for: Very subtle effects
```

---

## Debug Checklist

When animations feel slow or janky:

```
1. Performance
   ☐ Check FPS in DevTools (should be 60)
   ☐ Check GPU usage (transform vs top/left)
   ☐ Check for red triangles in timeline
   
2. Physics
   ☐ Check velocity calculations (console.log)
   ☐ Check easing function (is it correct?)
   ☐ Check clamp ranges (too restrictive?)
   
3. Events
   ☐ Check event listener count
   ☐ Check for duplicate listeners
   ☐ Check timeout accumulation
   
4. Browser
   ☐ Test in different browser
   ☐ Check hardware acceleration settings
   ☐ Check display refresh rate (60Hz vs 144Hz)
```

---

## Quick Code Snippets

### Get Current Velocity
```javascript
function getVelocity(current, previous) {
    return Math.abs(current - previous);
}

window.addEventListener("mousemove", (e) => {
    const vx = getVelocity(e.clientX, lastX);
    const vy = getVelocity(e.clientY, lastY);
    console.log(`Velocity: ${vx}, ${vy}`);
    lastX = e.clientX;
    lastY = e.clientY;
});
```

### Adjust Spring Stiffness
```javascript
// Slower spring (more lag)
gsap.quickTo(element, "x", { duration: 1.5, ease: "power4" });

// Faster spring (responsive)
gsap.quickTo(element, "x", { duration: 0.5, ease: "power4" });
```

### Custom Clamp Function
```javascript
function clamp(min, max, value) {
    return Math.max(min, Math.min(max, value));
}

// Usage
scale = clamp(0.8, 1.2, calculatedValue);
```

### Detect Mobile
```javascript
const isMobile = window.innerWidth < 768;

if (isMobile) {
    // Lighter animations
    circleMouseFollower();
} else {
    // Full animations
    smoothFollower();
    mouseChaptaKaro();
}
```

---

## Resources

### Further Reading
- **GSAP Documentation**: https://gsap.com/docs
- **Locomotive Scroll Docs**: https://www.lokomotivemtl.com
- **MDN Easing Functions**: https://developer.mozilla.org/docs/Web/CSS/easing-function
- **Spring Physics**: https://en.wikipedia.org/wiki/Harmonic_oscillator

### Tools
- Chrome DevTools Performance Tab
- Firefox Profiler
- Webpagetest.org
- Lighthouse

---

**Document Version**: 1.0  
**Last Updated**: 2024  
**For**: Portfolio Animation System Documentation

