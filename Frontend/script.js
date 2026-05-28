// ======================================================
// LOCOMOTIVE SCROLL
// ======================================================

const scroll = new LocomotiveScroll({
    el: document.querySelector("#main"),
    smooth: true
});

// Force update after full page load
window.addEventListener("load", () => {
    scroll.update();
});

// Handle font loading layout shifts
if (document.fonts) {
    document.fonts.ready.then(() => {
        scroll.update();
    });
}

//Paper Infograpgic 
const infographic = document.querySelector("#paper-main-hero-img");
if (infographic) {
    infographic.addEventListener("load",() =>{
        if (scroll) scroll.update();
    }); 
}

// ======================================================
// GLOBAL VARIABLES
// ======================================================

var timeout;


// ======================================================
// LOADING ANIMATION
// ======================================================

function loadingAnimation() {

    var tl = gsap.timeline();

    tl.from(".loader-anim", {
        y: 150,
        stagger: 0.2,
        duration: 0.8,
        ease: "power4.out"
    })

        .to(".loader-anim", {
            opacity: 0,
            delay: 0.5,
            stagger: -0.1,
            duration: 0.5
        })

        .to("#loader", {
    height: 0,
    duration: 1,
    ease: "expo.inOut",

    onStart: function () {
        firstPageAnim();
    }
})

            .call(function () {
             firstPageAnim();
             }, null, "-=1.2");
}


// ======================================================
// FIRST PAGE ANIMATION
// ======================================================

function firstPageAnim() {

    const isMobile = window.innerWidth < 768;

    gsap.to(".boundingelem", {
        y: 0,
        ease: "power4.out",
        duration: isMobile ? 0.8 : 1,
        stagger: isMobile ? 0.05 : 0.08
    });

    gsap.from("#herofooter", {
        y: 10,
        opacity: 0,
        duration: 0.6,
        ease: "power2.out",

        onComplete: function () {

            gsap.set("#herofooter", {
                clearProps: "all"
            });

            if (scroll) scroll.update();
        }

    });
    

}

// ======================================================
// MINI CIRCLE FOLLOWER
// ======================================================

function circleMouseFollower(xscale = 1, yscale = 1) {

    window.addEventListener("mousemove", function (dets) {

        document.querySelector("#minicircle").style.transform =
            `translate(${dets.clientX}px, ${dets.clientY}px) scale(${xscale}, ${yscale})`;

    });
}


// ======================================================
// MOUSE SQUEEZE EFFECT
// ======================================================

function mouseChaptaKaro() {

    var xscale = 1;
    var yscale = 1;

    var xprev = 0;
    var yprev = 0;

    window.addEventListener("mousemove", function (dets) {

        clearTimeout(timeout);

        // Calculate velocity and clamp it
        xscale = gsap.utils.clamp(.8, 1.2, dets.clientX - xprev);

        yscale = gsap.utils.clamp(.8, 1.2, dets.clientY - yprev);

        xprev = dets.clientX;
        yprev = dets.clientY;

        circleMouseFollower(xscale, yscale);

        timeout = setTimeout(() => {

            document.querySelector("#minicircle").style.transform =
                `translate(${dets.clientX}px, ${dets.clientY}px) scale(1,1)`;

        }, 100);

    });


    // Hide on mouse leave

    document.addEventListener("mouseleave", function () {

        gsap.to("#minicircle", {
            scale: 0,
            opacity: 0,
            duration: 0.3,
            ease: "power2.out"
        });

    });


    // Show on mouse enter

    document.addEventListener("mouseenter", function () {

        gsap.to("#minicircle", {
            scale: 1,
            opacity: 1,
            duration: 0.3,
            ease: "power2.out"
        });

    });
}


// ======================================================
// SMOOTH FOLLOWER
// ======================================================

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

    let xprev = 0;
    let yprev = 0;


    window.addEventListener("mousemove", function (dets) {

        // Position update

        xTo(dets.clientX);
        yTo(dets.clientY);


        // Stretch effect

        let xdiff = dets.clientX - xprev;
        let ydiff = dets.clientY - yprev;

        let xscale = gsap.utils.clamp(
            0.8,
            1.2,
            1 + Math.abs(xdiff) * 0.01
        );

        let yscale = gsap.utils.clamp(
            0.8,
            1.2,
            1 - Math.abs(ydiff) * 0.01
        );

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


// ======================================================
// IMAGE HOVER EFFECT
// ======================================================

document.querySelectorAll(".elem").forEach(function (elem) {

    var rotate = 0;
    var diffrot = 0;


    elem.addEventListener("mouseleave", function () {

        gsap.to(elem.querySelector("img"), {
            opacity: 0,
            ease: Power3
        });

    });


    elem.addEventListener("mousemove", function (dets) {

        var diff =
            dets.clientY - elem.getBoundingClientRect().top;

        diffrot = dets.clientX - rotate;
        rotate = dets.clientX;

        gsap.to(elem.querySelector("img"), {
            opacity: 1,
            ease: Power3,
            top: diff,
            left: dets.clientX,
            rotate: gsap.utils.clamp(-20, 20, diffrot * 0.5)
        });

    });

});

// ======================================================
// FUNCTION CALLS
// ======================================================

loadingAnimation();

circleMouseFollower();

mouseChaptaKaro();

smoothFollower();