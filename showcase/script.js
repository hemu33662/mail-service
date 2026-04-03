// Navbar Scroll Transition
window.addEventListener('scroll', function() {
    const nav = document.getElementById('navbar');
    if (window.scrollY > 50) {
        nav.classList.add('scrolled');
    } else {
        nav.classList.remove('scrolled');
    }
});

// API Documentation Tab Logic
function showTab(lang) {
    const contents = document.querySelectorAll('.tab-content');
    const buttons = document.querySelectorAll('.tab-btn');
    
    contents.forEach(content => {
        content.classList.add('hidden');
    });
    
    buttons.forEach(btn => {
        btn.classList.remove('active');
    });
    
    document.getElementById(lang + '-tab').classList.remove('hidden');
    
    // Find button that has the same text as language parameter
    buttons.forEach(btn => {
        if(btn.innerText.toLowerCase() === lang.toLowerCase()) {
            btn.classList.add('active');
        }
    });

    // Reset AOS for the new content to trigger entry animation
    AOS.refresh();
}

// Particle Effect (Subtle)
const canvas = document.createElement('canvas');
canvas.id = 'hero-canvas';
document.querySelector('.hero').appendChild(canvas);

const ctx = canvas.getContext('2d');
let particles = [];

function resize() {
    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;
}
window.addEventListener('resize', resize);
resize();

class Particle {
    constructor() {
        this.x = Math.random() * canvas.width;
        this.y = Math.random() * canvas.height;
        this.size = Math.random() * 2 + 1;
        this.speedX = Math.random() * 1 - 0.5;
        this.speedY = Math.random() * 1 - 0.5;
        this.opacity = Math.random() * 0.5 + 0.1;
    }
    update() {
        this.x += this.speedX;
        this.y += this.speedY;
        if (this.x > canvas.width) this.x = 0;
        if (this.x < 0) this.x = canvas.width;
        if (this.y > canvas.height) this.y = 0;
        if (this.y < 0) this.y = canvas.height;
    }
    draw() {
        ctx.fillStyle = `rgba(99, 102, 241, ${this.opacity})`;
        ctx.beginPath();
        ctx.arc(this.x, this.y, this.size, 0, Math.PI * 2);
        ctx.fill();
    }
}

function init() {
    for (let i = 0; i < 50; i++) {
        particles.push(new Particle());
    }
}
init();

function animate() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    particles.forEach(p => {
        p.update();
        p.draw();
    });
    requestAnimationFrame(animate);
}
animate();

// Stylistic adjustment for canvas
canvas.style.position = 'absolute';
canvas.style.top = '0';
canvas.style.left = '0';
canvas.style.zIndex = '-1';
canvas.style.pointerEvents = 'none';
