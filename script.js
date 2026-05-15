const scroll = new LocomotiveScroll({
    el: document.querySelector('#main'),
    smooth: true
});
setTimeout(() => {
    scroll.update();
}, 1000);

var timeout;
function circleMouseFollower(){
    window.addEventListener("mousemove", function(dets){
        document.querySelector("#minicircle").style.transform =`translate(${dets.clientX}px, ${dets.clientY}px)`})
}


function loadingAnimation() {
    var tl = gsap.timeline();

    // 1. Reveal the loader text from bottom
    tl.from(".loader-anim", {
        y: 150,
        stagger: 0.2,
        duration: 0.8,
        ease: "power4.out"
    })
    // 2. Wait a small moment, then fade text out
    .to(".loader-anim", {
        opacity: 0,
        delay: 0.5,
        stagger: -0.1, // Reverse stagger for a cool effect
        duration: 0.5
    })
    // 3. Lift the black curtain
    .to("#loader", {
        height: 0,
        duration: 1.2,
        ease: "expo.inOut"
    })
    // 4. Simultaneously trigger the main page hero animation
    .call(function() {
        firstPageAnim();
    }, null, "-=0.8"); // Starts hero anim 0.8s before loader finishes
}

// REMOVE your direct call to firstPageAnim() at the bottom
// REPLACING it with:
loadingAnimation();
// teeno element ko sleect karo, uske baad teeno par ek mousemove lagao, jab mousemove ho to ye pata karo ki mouse kaha par hai, jiska matlab hai mouse ki x and y position pata karo, ab mouse ki x y position ke badle us image ko show karo and us image ko move karo, move karte waqt rotate karo, and jaise jaise mouse tez chale waise waise rotation bhi tez ho jaye
//squeezz the circle as the mouse moves faster, and release it as the mouse slows down. This will create a dynamic and interactive experience for the user, making the circle feel more responsive to their movements. You can achieve this by calculating the speed of the mouse movement and adjusting the scale of the circle accordingly
function mouseChaptaKaro(){
    var xscale = 1;
    var yscale = 1;
    var xprev = 0;
    var yprev = 0;

    window.addEventListener("mousemove", function(dets){
        clearTimeout(timeout);

        // Calculate velocity and clamp it
        xscale = gsap.utils.clamp(.8, 1.2, dets.clientX - xprev);
        yscale = gsap.utils.clamp(.8, 1.2, dets.clientY - yprev);

        xprev = dets.clientX;
        yprev = dets.clientY;
        
        circleMouseFollower(xscale, yscale);
        
        timeout = setTimeout(() => {
            document.querySelector('#minicircle').style.transform = `translate(${dets.clientX}px,${dets.clientY}px) scale(1,1)`;
        }, 100);
    });

    // FIX: Handle cursor leaving the browser window
    document.addEventListener("mouseleave", function() {
        gsap.to("#minicircle", {
            scale: 0,
            opacity: 0,
      
            duration: 0.3,
            ease: "power2.out"
        });
    });

    document.addEventListener("mouseenter", function() {
        gsap.to("#minicircle", {
            scale: 1,
            opacity: 1,
            duration: 0.3,
            ease: "power2.out"
        });
    });
}

function smoothFollower() {
    const circle = document.querySelector("#minicircle");
    
    // The "Sweet Spot" for high-end sites is actually between 0.1 and 0.15
    // Power4 ease creates a very fast start but a soft "landing"
    const xTo = gsap.quickTo(circle, "x", { duration: 0.9, ease: "power4" });
    const yTo = gsap.quickTo(circle, "y", { duration: 0.9, ease: "power4" });

    let xprev = 0;
    let yprev = 0;

    window.addEventListener("mousemove", function(dets) {
        // Position update
        xTo(dets.clientX);
        yTo(dets.clientY);

        // Advanced Squeeze Math
        // We calculate the delta (change) and use it to skew the circle
        let xdiff = dets.clientX - xprev;
        let ydiff = dets.clientY - yprev;

        // The 0.1 multiplier controls how much it "stretches"
        // The clamp keeps the stretch within realistic limits
        let xscale = gsap.utils.clamp(0.8, 1.2, 1 + Math.abs(xdiff) * 0.01);
        let yscale = gsap.utils.clamp(0.8, 1.2, 1 - Math.abs(ydiff) * 0.01);

        xprev = dets.clientX;
        yprev = dets.clientY;

        gsap.to(circle, {
            scaleX: xscale,
            scaleY: yscale,
            duration: 0.1,
            ease: "power3"
        });
    });
}

function firstPageAnim(){
    var tl = gsap.timeline();
    tl.from("#nav",{
        y : '-10',
        opacity : 0,
        duration : 1.5,
        ease: Expo.easeInOut
    })
    .to(".boundingelem",{
        y:0,
        ease: Expo.easeInOut,
        duration: 2,
        delay: -1,
        stagger: .2
    })
    .from("#herofooter",{
        y: -10,
        opacity: 0,
        duration: 1.5,
        delay: -1,
        ease: Expo.easeInOut
    })
}

function circleMouseFollower(xscale, yscale){
    window.addEventListener("mousemove", function(dets){
        document.querySelector("#minicircle").style.transform =`translate(${dets.clientX}px, ${dets.clientY}px) scale(${xscale}, ${yscale})`
    })
}

document.querySelectorAll(".elem").forEach(function(elem){
    var rotate = 0;
    var diffrot = 0;

    elem.addEventListener("mouseleave",function(dets){
       
        gsap.to(elem.querySelector("img"),{
            opacity: 0,
            ease: Power3,
           });
    });

elem.addEventListener("mousemove", function (dets) {
    var diff = dets.clientY - elem.getBoundingClientRect().top;
    diffrot = dets.clientX - rotate;
    rotate = dets.clientX;
    gsap.to(elem.querySelector("img"), {
      opacity: 1,
      ease: Power3,
      top: diff,
      left: dets.clientX,
      rotate: gsap.utils.clamp(-20, 20, diffrot * 0.5),
    });
  });
});

circleMouseFollower();
firstPageAnim();
mouseChaptaKaro();
smoothFollower();