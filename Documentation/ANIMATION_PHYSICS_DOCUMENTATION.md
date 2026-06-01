# Portfolio Animation System - Technical Documentation

## Table of Contents
1. [Overview](#overview)
2. [Physics Foundation](#physics-foundation)
3. [Locomotive Scroll - Smooth Scroll](#locomotive-scroll---smooth-scroll)
4. [Loading Animation](#loading-animation)
5. [Circle Mouse Follower](#circle-mouse-follower)
6. [Mouse Squeeze Effect (mouseChaptaKaro)](#mouse-squeeze-effect-mousechaptakaro)
7. [Smooth Follower](#smooth-follower)
8. [Image Hover Effect](#image-hover-effect)
9. [Integration & Performance](#integration--performance)

---

## Overview

This animation system creates a sophisticated, physics-based interactive experience using:
- **GSAP** (GreenSock Animation Platform) - Animation engine
- **Locomotive Scroll** - Smooth scrolling
- **Physics Concepts** - Velocity, acceleration, damping, easing

### Library Dependencies
```javascript
// GSAP 3.13.0 - Animation & physics simulation
// Locomotive Scroll 3.5.4 - Smooth parallax scrolling
```

---

## Physics Foundation

### Core Physics Concepts Implemented

#### 1. **Velocity Calculation**
Velocity = Change in Position / Change in Time

```javascript
let xdiff = dets.clientX - xprev;  // Δx (change in position)
let ydiff = dets.clientY - yprev;  // Δy (change in position)
xprev = dets.clientX;
yprev = dets.clientY;
```

**Physics Applied**: Tracks instantaneous velocity to create momentum-based effects.

#### 2. **Clamping (Value Bounding)**
Limits a value within a defined range: `clamp(min, max, value)`

```javascript
xscale = gsap.utils.clamp(0.8, 1.2, calculatedValue);
```

**Physics Applied**: Prevents infinite acceleration by capping scale values between 0.8x and 1.2x, simulating friction/resistance.

#### 3. **Easing Functions**
Non-linear interpolation that simulates natural motion acceleration/deceleration.

```javascript
ease: "power1.out"    // Linear deceleration
ease: "power4.out"    // Exponential deceleration
ease: "cubic-bezier(0.19, 1, 0.22, 1)"  // Custom bezier curve
```

**Physics Applied**: 
- `power1.out` → Constant deceleration
- `power4.out` → Rapid initial slowdown (more responsive)

#### 4. **Spring Physics (quickTo)**
GSAP's `quickTo()` implements spring physics with configurable tension and damping.

```javascript
const xTo = gsap.quickTo(circle, "x", {
    duration: 0.9,      // Spring tension (lower = tighter)
    ease: "power4"      // Damping (higher power = less oscillation)
});
```

**Physics Formula**:
```
F = -kx  (Hooke's Law)
Position = Position + Velocity * dt - SpringForce * damping
```

---

## Locomotive Scroll - Smooth Scroll

### What It Does
Replaces native browser scrolling with smooth, GPU-accelerated scrolling with parallax effects.

### Implementation
```javascript
const scroll = new LocomotiveScroll({
    el: document.querySelector("#main"),
    smooth: true  // Enable smooth scrolling
});
```

### Physics Mechanism
```
V_current = V_target * (1 - friction)
Position += V_current
```

**Parameters**:
- Smoothing factor (internal): ~0.08-0.1 (8-10% per frame)
- Works with scroll velocity to create momentum

### Update Triggers
```javascript
// After page load
window.addEventListener("load", () => {
    scroll.update();
});

// After fonts load (prevents layout shifts)
document.fonts.ready.then(() => {
    scroll.update();
});

// After images load
infographic.addEventListener("load", () => {
    if (scroll) scroll.update();
});
```

### Visual Effect
```
Without Smooth Scroll:    With Smooth Scroll (Locomotive):
Scroll ----/----/----     Scroll ~~~~~___~~~~~___
(Jumpy)                   (Fluid with momentum)
```

---

## Loading Animation

### Animation Flow
```
Timeline Sequence:
1. Elements slide up from below (y: 150px → 0)
2. Elements fade out after delay
3. Loader container shrinks (height: 100vh → 0)
4. Trigger first page animation
```

### Code Breakdown
```javascript
function loadingAnimation() {
    var tl = gsap.timeline();
    
    // PHASE 1: Slide up with stagger
    tl.from(".loader-anim", {
        y: 150,              // Start 150px below
        stagger: 0.2,        // Delay between each element: 200ms
        duration: 0.8,       // Animation duration
        ease: "power4.out"   // Exponential easing
    })
    
    // PHASE 2: Fade out with reverse stagger
    .to(".loader-anim", {
        opacity: 0,
        delay: 0.5,
        stagger: -0.1,       // Negative = reverse order
        duration: 0.5
    })
    
    // PHASE 3: Collapse loader
    .to("#loader", {
        height: 0,
        duration: 1,
        ease: "expo.inOut"   // Exponential in and out
    })
}
```

### Physics Parameters
| Parameter | Value | Effect |
|-----------|-------|--------|
| `y` | 150px | Initial displacement |
| `stagger` | 0.2s | Creates wave effect |
| `ease: power4.out` | - | 4th-order polynomial deceleration |
| `ease: expo.inOut` | - | Exponential acceleration + deceleration |

### Timeline Visualization
```
Time (ms)    0     200    400    600    800   1000  1200  1400
             |-----|------|------|------|-----|-----|------|
Element 1    [====]=========>
Element 2         [====]=========>
Element 3              [====]=========>
Fade                              [=====]
Loader Collapse                              [=======]
```

---

## Circle Mouse Follower

### Purpose
Creates a simple circular cursor that follows mouse movement.

### Implementation
```javascript
function circleMouseFollower(xscale = 1, yscale = 1) {
    window.addEventListener("mousemove", function (dets) {
        document.querySelector("#minicircle").style.transform =
            `translate(${dets.clientX}px, ${dets.clientY}px) 
             scale(${xscale}, ${yscale})`;
    });
}
```

### Physics Applied
- **Direct Position Mapping**: No delay/easing (instant response)
- **Scale Parameters**: Allow external stretch effects
- **Transform 3D**: Uses GPU acceleration (`translate3d`)

### Performance Optimization
```javascript
// Direct manipulation bypasses animation queue
// Updates on every mousemove event (~60fps on 60Hz display)
```

---

## Mouse Squeeze Effect (mouseChaptaKaro)

### What It Does
Creates a "squeeze" or "stretch" effect on the cursor based on movement velocity.

### Physics Principle: Velocity-Based Deformation

```
Velocity Calculation:
Δx = Current_X - Previous_X
Δy = Current_Y - Previous_Y

Scale Calculation:
xscale = 1 + (velocity_x / reference_velocity)
yscale = 1 + (velocity_y / reference_velocity)

Then clamped: scale ∈ [0.8, 1.2]
```

### Code Analysis
```javascript
function mouseChaptaKaro() {
    var xscale = 1, yscale = 1;
    var xprev = 0, yprev = 0;
    
    window.addEventListener("mousemove", function (dets) {
        clearTimeout(timeout);
        
        // PHYSICS: Velocity-based scaling
        xscale = gsap.utils.clamp(0.8, 1.2, dets.clientX - xprev);
        yscale = gsap.utils.clamp(0.8, 1.2, dets.clientY - yprev);
        
        xprev = dets.clientX;
        yprev = dets.clientY;
        
        circleMouseFollower(xscale, yscale);
        
        // Reset to normal after 100ms of inactivity
        timeout = setTimeout(() => {
            document.querySelector("#minicircle").style.transform =
                `translate(${dets.clientX}px, ${dets.clientY}px) scale(1,1)`;
        }, 100);
    });
}
```

### Stretch Effect Animation

```
Fast movement right:
|●| → |◯|  (stretched horizontally)
 ↓
Velocity > 0 → xscale increases to 1.2

Slow/no movement:
|◯| → |●|  (returns to circle)
 ↓
After 100ms timeout → scale(1, 1)
```

### Physics Parameters
| Parameter | Value | Meaning |
|-----------|-------|---------|
| `clamp(min, max)` | 0.8, 1.2 | 20% compression → 20% stretch |
| `timeout` | 100ms | Inertia reset time |
| `dets.clientX - xprev` | Δx (pixels) | Velocity magnitude |

---

## Smooth Follower

### Purpose
Creates a smoothly animated cursor following with physics-based momentum and stretch.

### Core Physics: Spring Dynamics with Damping

```
Spring Physics Formula:
F = -k·x           (Restoring force proportional to displacement)
a = F/m = -k·x/m   (Acceleration from spring force)
v_new = v_old + a  (Update velocity)
x_new = x_old + v  (Update position)

GSAP Implementation:
quickTo(property, duration, ease) ≈ Damped harmonic oscillator
```

### Code Breakdown

#### Part 1: Setup Spring Physics
```javascript
const circle = document.querySelector("#minicircle");

const xTo = gsap.quickTo(circle, "x", {
    duration: 0.9,      // Spring stiffness (k parameter)
    ease: "power4"      // Damping coefficient (friction)
});

const yTo = gsap.quickTo(circle, "y", {
    duration: 0.9,
    ease: "power4"
});
```

**Physics Interpretation**:
- `duration: 0.9s` = Lower K (spring constant) → Slower response
- `ease: "power4"` = Increased damping → Prevents oscillation

#### Part 2: Velocity Tracking
```javascript
let xprev = 0, yprev = 0;

window.addEventListener("mousemove", function (dets) {
    // Target position (spring will pull towards this)
    xTo(dets.clientX);
    yTo(dets.clientY);
    
    // VELOCITY CALCULATION
    let xdiff = dets.clientX - xprev;
    let ydiff = dets.clientY - yprev;
    
    xprev = dets.clientX;
    yprev = dets.clientY;
```

#### Part 3: Momentum-Based Stretch
```javascript
    // Physics: Deformation based on velocity magnitude
    let xscale = gsap.utils.clamp(
        0.8, 1.2,
        1 + Math.abs(xdiff) * 0.01  // 1% stretch per pixel of velocity
    );
    
    let yscale = gsap.utils.clamp(
        0.8, 1.2,
        1 - Math.abs(ydiff) * 0.01  // Inverse for Y (compression)
    );
    
    // Apply deformation with damping
    gsap.to(circle, {
        scaleX: xscale,
        scaleY: yscale,
        duration: 0.1,
        ease: "power3"
    });
});
```

### Stretch Direction Logic

```
Horizontal Movement:
Cursor moves right → xscale increases (stretch horizontally)
Cursor moves left  → xscale increases (stretch horizontally)

Vertical Movement:
Cursor moves down  → yscale decreases (compress vertically)
Cursor moves up    → yscale decreases (compress vertically)

Formula Basis:
xscale = 1 + |Δx| × 0.01  → Always stretches when moving horizontally
yscale = 1 - |Δy| × 0.01  → Always compresses when moving vertically
```

### Physics Timeline
```
Frame 1: Cursor at (100, 100)
Frame 2: Cursor at (150, 120) → Δx=50, Δy=20
         xscale = 1 + 50×0.01 = 1.5 (clamped to 1.2)
         yscale = 1 - 20×0.01 = 0.8

Frame 3: quickTo animates to new position over 0.9s
         Stretch smoothly transitions with ease: power3
```

---

## Image Hover Effect

### Purpose
Creates an image that appears and follows cursor movement with rotation based on velocity direction.

### Physics: Rotation Based on Acceleration

```javascript
document.querySelectorAll(".elem").forEach(function (elem) {
    var rotate = 0;      // Current rotation angle
    var diffrot = 0;     // Rotation delta
    
    elem.addEventListener("mouseleave", function () {
        gsap.to(elem.querySelector("img"), {
            opacity: 0,
            ease: Power3
        });
    });
    
    elem.addEventListener("mousemove", function (dets) {
        // Calculate vertical distance from element top
        var diff = dets.clientY - elem.getBoundingClientRect().top;
        
        // PHYSICS: Rotational velocity
        diffrot = dets.clientX - rotate;  // Δrotation = Δposition
        rotate = dets.clientX;             // Update previous position
        
        gsap.to(elem.querySelector("img"), {
            opacity: 1,
            ease: Power3,
            top: diff,              // Vertical tracking
            left: dets.clientX,     // Horizontal tracking
            rotate: gsap.utils.clamp(-20, 20, diffrot * 0.5)  // Rotation from velocity
        });
    });
});
```

### Physics Breakdown

#### Rotation Physics
```
Angular Velocity (ω):
ω = Δangle / Δtime
ω ≈ (Current_X - Previous_X) × Sensitivity
ω ≈ diffrot × 0.5

Rotation Clamping:
rotate ∈ [-20°, +20°]  // Prevents excessive rotation
```

#### Movement Mapping
```
User moves cursor right:
→ diffrot = positive value
→ diffrot × 0.5 = rotation angle (right tilt)
→ Image tilts right

User moves cursor left:
→ diffrot = negative value
→ diffrot × 0.5 = rotation angle (left tilt)
→ Image tilts left
```

### Visual Effect Timeline
```
User Interaction:
1. Mouse enters element  → Image appears (opacity: 0→1)
2. Mouse moves right     → Image tilts right, follows cursor
3. Mouse moves left      → Image tilts left, follows cursor
4. Mouse leaves element  → Image fades out (opacity: 1→0)
```

---

## Integration & Performance

### Execution Order
```javascript
// 1. Initialize Locomotive Scroll
const scroll = new LocomotiveScroll({ ... });

// 2. Setup animation triggers
scroll.update();  // When fonts load, images load, page loads

// 3. Trigger entry animation
loadingAnimation();

// 4. Initialize cursor effects
circleMouseFollower();      // Base follower
mouseChaptaKaro();          // Squeeze effect
smoothFollower();           // Spring-based follower

// 5. Image hover listeners (auto-attached to all .elem)
// Built-in at end of script
```

### Performance Optimization Techniques

#### 1. **GPU Acceleration**
```javascript
// Uses transform (GPU) instead of top/left (CPU)
transform: `translate(${x}px, ${y}px)`
```

#### 2. **Event Throttling**
```javascript
// Clears existing timeout before setting new one
clearTimeout(timeout);
timeout = setTimeout(() => { ... }, 100);
// Prevents accumulation of delayed function calls
```

#### 3. **Will-Change Property**
```css
#minicircle {
    will-change: transform;  /* Hints browser to optimize */
    pointer-events: none;     /* Prevents layout recalculation */
}
```

#### 4. **Transition: None**
```css
#minicircle {
    transition: none !important;  /* Instant position updates */
}
```

### Frame Rate Impact
```
60 FPS Display:
- Each mousemove event: ~16ms apart
- Animation updates: Every frame via GSAP
- Smooth result: Continuous motion at 60fps
- Target: Aim for 60fps (16.67ms per frame)
```

### Memory Management
```javascript
// Proper cleanup prevents memory leaks
clearTimeout(timeout);           // Before setting new timeout
document.addEventListener()      // Standard event binding
gsap.quickTo()                  // Reuses animation queue
```

---

## Usage Examples

### Example 1: Disabling Smooth Follower on Mobile
```javascript
// Mobile devices often struggle with smooth animations
if (window.innerWidth < 768) {
    smoothFollower = () => {};  // Override function
} else {
    smoothFollower();
}
```

### Example 2: Custom Loading Animation Duration
```javascript
loadingAnimation();

// Adjust speeds for slower connections
tl.to("#loader", {
    height: 0,
    duration: 1.5,  // Increased from 1s
    ease: "expo.inOut"
});
```

### Example 3: Disable Cursor Effects on Touch Devices
```javascript
if (touch is detected) {
    circleMouseFollower = () => {};
    mouseChaptaKaro = () => {};
    smoothFollower = () => {};
}
```

---

## Physics Formulas Reference

### Easing Equations

#### Power1 (Linear)
```
t = normalized time [0,1]
easeOut(t) = t
Result: Constant velocity deceleration
```

#### Power4 (Quartic)
```
easeOut(t) = 1 - (1-t)⁴
Result: Rapid deceleration, then slow tail
```

#### Expo (Exponential)
```
easeInOut(t) = t < 0.5 ? 2^(20t-10)/2 : 1 - 2^(-20t+10)/2
Result: Slow start, accelerate, then decelerate
```

### Velocity Scaling
```
Scale = Clamp(Min, Max, BaseValue + Δposition × Sensitivity)

Example:
xscale = Clamp(0.8, 1.2, 1 + |Δx| × 0.01)
If Δx = 50px: xscale = 1 + 0.5 = 1.5 → clamped to 1.2
```

### Spring Dynamics
```
Natural Frequency (ω₀):
ω₀ ∝ √(k/m) ∝ 1/duration

Damping Ratio (ζ):
ζ ∝ ease function power level

Underdamped (ζ < 1):   Oscillates before settling
Critically damped (ζ = 1): Fastest settlement without overshoot
Overdamped (ζ > 1):    Slow, smooth approach
```

---

## Browser Compatibility

| Feature | Chrome | Firefox | Safari | Edge |
|---------|--------|---------|--------|------|
| GSAP | ✅ All | ✅ All | ✅ All | ✅ All |
| Locomotive Scroll | ✅ 60+ | ✅ 55+ | ✅ 10+ | ✅ 15+ |
| CSS Transforms | ✅ 26+ | ✅ 16+ | ✅ 5+ | ✅ 12+ |
| RequestAnimationFrame | ✅ 24+ | ✅ 4+ | ✅ 6+ | ✅ 10+ |

---

## Debugging Tips

### Enable Animation Timings
```javascript
// Log frame-by-frame animation values
gsap.to(circle, {
    x: 100,
    onUpdate: function() {
        console.log("Current X:", circle.style.transform);
    }
});
```

### Check Physics Parameters
```javascript
// Verify velocity calculations
window.addEventListener("mousemove", (e) => {
    console.log("Velocity X:", e.clientX - previousX);
    console.log("Velocity Y:", e.clientY - previousY);
});
```

### Performance Profiling
```javascript
// Chrome DevTools: Performance tab
// Check FPS meter:
console.log("FPS:", 1000 / (currentTime - lastTime));

// Check for layout thrashing:
// Avoid reading then writing DOM repeatedly
```

---

## Conclusion

This animation system combines several physics principles:
1. **Spring Physics** - Smooth natural motion
2. **Velocity Tracking** - Momentum-based effects
3. **Damping** - Prevents oscillation
4. **Clamping** - Limits extreme values
5. **Easing Functions** - Realistic acceleration curves

The result is a smooth, responsive, physics-aware animation system that feels natural and performant.

