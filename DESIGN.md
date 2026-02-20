# Ledger — Screen Designs

Below are alternative HTML design proposals for the **same main screen** of Ledger.

---

## Design: Monolithic Anodized Slabwork

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ledger Monolith</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;700;900&family=JetBrains+Mono:wght@400;500;700&display=swap" rel="stylesheet">
    <style>
        :root {
            --bg: #0a0a0a;
            --slab: #161616;
            --slab-light: #1e1e1e;
            --accent: #3b82f6;
            --text-main: #e5e5e5;
            --text-dim: #737373;
            --border: rgba(255, 255, 255, 0.08);
            --chamfer: rgba(255, 255, 255, 0.12);
            --grain: url("data:image/svg+xml,%3Csvg viewBox='0 0 200 200' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='noiseFilter'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.65' numOctaves='3' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23noiseFilter)'/%3E%3C/svg%3E");
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            -webkit-font-smoothing: antialiased;
        }

        body {
            background-color: var(--bg);
            color: var(--text-main);
            font-family: 'Inter', sans-serif;
            display: flex;
            justify-content: center;
            min-height: 100vh;
            overflow-x: hidden;
        }

        /* Anodized Texture Overlay */
        body::before {
            content: "";
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background-image: var(--grain);
            opacity: 0.04;
            pointer-events: none;
            z-index: 100;
        }

        .phone-frame {
            width: 100%;
            max-width: 430px;
            min-height: 100vh;
            background: var(--bg);
            padding: 24px;
            position: relative;
            padding-bottom: 120px;
        }

        header {
            margin-bottom: 32px;
            animation: fadeInDown 0.8s cubic-bezier(0.16, 1, 0.3, 1);
        }

        .brand {
            font-weight: 900;
            font-size: 14px;
            letter-spacing: 0.3em;
            text-transform: uppercase;
            color: var(--text-dim);
        }

        /* Monolithic Slab: Balance Card */
        .balance-monolith {
            background: linear-gradient(145deg, var(--slab-light), var(--slab));
            border: 1px solid var(--border);
            border-top: 1px solid var(--chamfer);
            border-left: 1px solid var(--chamfer);
            border-radius: 4px;
            padding: 40px 24px;
            margin-bottom: 32px;
            box-shadow: 20px 20px 60px #050505, -5px -5px 20px rgba(255,255,255,0.02);
            position: relative;
            overflow: hidden;
            animation: slideUp 0.8s cubic-bezier(0.16, 1, 0.3, 1);
        }

        .balance-monolith::after {
            content: "";
            position: absolute;
            top: 0;
            right: 0;
            width: 100px;
            height: 100%;
            background: linear-gradient(90deg, transparent, rgba(255,255,255,0.02));
            pointer-events: none;
        }

        .label-small {
            font-family: 'JetBrains Mono', monospace;
            font-size: 11px;
            color: var(--text-dim);
            text-transform: uppercase;
            letter-spacing: 0.1em;
            margin-bottom: 8px;
            display: block;
        }

        .balance-amount {
            font-family: 'JetBrains Mono', monospace;
            font-size: 42px;
            font-weight: 700;
            letter-spacing: -0.05em;
            color: #fff;
        }

        .balance-amount span {
            color: var(--text-dim);
            font-size: 24px;
        }

        /* Feature Grid */
        .feature-grid {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 16px;
            margin-bottom: 48px;
            animation: slideUp 1s cubic-bezier(0.16, 1, 0.3, 1) forwards;
        }

        .tile {
            aspect-ratio: 1 / 1;
            background: var(--slab);
            border: 1px solid var(--border);
            border-radius: 4px;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            padding: 20px;
            transition: all 0.3s ease;
            cursor: pointer;
            position: relative;
        }

        .tile:hover {
            border-color: var(--accent);
            transform: translateY(-4px);
            box-shadow: 0 10px 30px rgba(0,0,0,0.5);
        }

        .tile-icon {
            width: 24px;
            height: 24px;
            border: 1.5px solid var(--text-dim);
            border-radius: 2px;
        }

        .tile-name {
            font-size: 13px;
            font-weight: 700;
            color: var(--text-main);
            letter-spacing: 0.02em;
        }

        .sync-status {
            position: absolute;
            top: 20px;
            right: 20px;
            width: 6px;
            height: 6px;
            background: #10b981;
            border-radius: 50%;
            box-shadow: 0 0 10px #10b981;
        }

        /* Transactions Slab */
        .transactions-section {
            animation: slideUp 1.2s cubic-bezier(0.16, 1, 0.3, 1) forwards;
        }

        .section-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }

        .section-header h2 {
            font-size: 12px;
            text-transform: uppercase;
            letter-spacing: 0.15em;
            color: var(--text-dim);
        }

        .see-all {
            font-family: 'JetBrains Mono', monospace;
            font-size: 11px;
            color: var(--accent);
            text-decoration: none;
            font-weight: 700;
        }

        .transaction-list {
            display: flex;
            flex-direction: column;
            gap: 1px;
            background: var(--border);
            border-radius: 4px;
            overflow: hidden;
            border: 1px solid var(--border);
        }

        .transaction-item {
            background: var(--bg);
            padding: 16px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            transition: background 0.2s ease;
        }

        .transaction-item:hover {
            background: var(--slab);
        }

        .tx-info .tx-name {
            font-size: 14px;
            font-weight: 600;
            margin-bottom: 4px;
        }

        .tx-info .tx-date {
            font-size: 11px;
            font-family: 'JetBrains Mono', monospace;
            color: var(--text-dim);
        }

        .tx-amount {
            font-family: 'JetBrains Mono', monospace;
            font-size: 14px;
            font-weight: 700;
        }

        .tx-amount.neg { color: #f43f5e; }
        .tx-amount.pos { color: #10b981; }

        /* Floating Add Button */
        .fab {
            position: fixed;
            bottom: 32px;
            right: 32px;
            width: 64px;
            height: 64px;
            background: var(--text-main);
            border: none;
            border-radius: 4px;
            color: var(--bg);
            font-size: 32px;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            box-shadow: 0 15px 40px rgba(0,0,0,0.6), inset 0 2px 2px rgba(255,255,255,0.5);
            transition: transform 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
            z-index: 1000;
        }

        .fab:hover {
            transform: scale(1.1) rotate(90deg);
            background: #fff;
        }

        /* Animations */
        @keyframes slideUp {
            from { opacity: 0; transform: translateY(40px); }
            to { opacity: 1; transform: translateY(0); }
        }

        @keyframes fadeInDown {
            from { opacity: 0; transform: translateY(-20px); }
            to { opacity: 1; transform: translateY(0); }
        }

        /* Responsive */
        @media (max-width: 480px) {
            .phone-frame {
                padding: 20px;
            }
        }
    </style>
</head>
<body>

    <div class="phone-frame">
        <header>
            <div class="brand">Ledger</div>
        </header>

        <section class="balance-monolith">
            <span class="label-small">Total Liquidity</span>
            <div class="balance-amount"><span>$</span>12,482.50</div>
            <div style="margin-top: 20px; display: flex; gap: 15px;">
                <div style="font-family: 'JetBrains Mono'; font-size: 10px; color: #10b981;">+ 2.4% MONTHLY</div>
                <div style="font-family: 'JetBrains Mono'; font-size: 10px; color: var(--text-dim);">NO LEAKS DETECTED</div>
            </div>
        </section>

        <nav class="feature-grid">
            <div class="tile">
                <div class="tile-icon" style="border-style: dashed;"></div>
                <div class="tile-name">RECURRING</div>
            </div>
            <div class="tile">
                <div class="tile-icon" style="background: var(--text-dim); opacity: 0.3;"></div>
                <div class="tile-name">SUMMARY</div>
            </div>
            <div class="tile">
                <div class="tile-icon" style="border-width: 3px;"></div>
                <div class="tile-name">BUDGETS</div>
            </div>
            <div class="tile">
                <div class="sync-status"></div>
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" style="color: var(--text-dim)">
                    <path d="M12 10v4m-2-2h4m-7 5a9 9 0 1118 0 9 9 0 01-18 0z" />
                </svg>
                <div class="tile-name">CLOUD SYNC</div>
            </div>
        </nav>

        <section class="transactions-section">
            <div class="section-header">
                <h2>Recent Transactions</h2>
                <a href="#" class="see-all">VIEW_ALL_LOGS</a>
            </div>
            <div class="transaction-list">
                <div class="transaction-item">
                    <div class="tx-info">
                        <div class="tx-name">Hardware Store</div>
                        <div class="tx-date">OCT 24 // 14:30</div>
                    </div>
                    <div class="tx-amount neg">-$124.00</div>
                </div>
                <div class="transaction-item">
                    <div class="tx-info">
                        <div class="tx-name">Client Payment</div>
                        <div class="tx-date">OCT 23 // 09:12</div>
                    </div>
                    <div class="tx-amount pos">+$4,200.00</div>
                </div>
                <div class="transaction-item">
                    <div class="tx-info">
                        <div class="tx-name">Subscription Service</div>
                        <div class="tx-date">OCT 21 // 23:59</div>
                    </div>
                    <div class="tx-amount neg">-$14.99</div>
                </div>
                <div class="transaction-item">
                    <div class="tx-info">
                        <div class="tx-name">Electric Utility</div>
                        <div class="tx-date">OCT 20 // 11:45</div>
                    </div>
                    <div class="tx-amount neg">-$88.20</div>
                </div>
            </div>
        </section>

        <button class="fab" aria-label="Add Transaction">+</button>
    </div>

    <script>
        // Simple interaction feedback
        document.querySelectorAll('.tile, .transaction-item').forEach(el => {
            el.addEventListener('click', () => {
                el.style.transform = 'scale(0.98)';
                setTimeout(() => {
                    el.style.transform = '';
                }, 100);
            });
        });
    </script>
</body>
</html>

```

## Design: Debossed Heavy-Pulp Relief

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ledger — Finance</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;700;900&family=JetBrains+Mono:wght@500&display=swap" rel="stylesheet">
    <style>
        :root {
            --paper-bg: #e5e5df;
            --paper-grain: #dadad2;
            --shadow-dark: rgba(0, 0, 0, 0.12);
            --shadow-light: rgba(255, 255, 255, 0.9);
            --accent: #2d312e;
            --text-main: #3a3a35;
            --text-muted: #7a7a70;
            --positive: #4a6741;
            --negative: #914d4d;
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            -webkit-font-smoothing: antialiased;
        }

        body {
            background-color: var(--paper-bg);
            font-family: 'Inter', sans-serif;
            color: var(--text-main);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            overflow-x: hidden;
            position: relative;
        }

        /* Paper Texture Overlay */
        body::before {
            content: "";
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            pointer-events: none;
            z-index: 100;
            opacity: 0.4;
            filter: url(#grain);
        }

        .app-container {
            width: 100%;
            max-width: 480px;
            padding: 40px 24px 120px 24px;
            animation: fadeIn 0.8s ease-out;
        }

        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(10px); }
            to { opacity: 1; transform: translateY(0); }
        }

        header {
            margin-bottom: 32px;
        }

        .logo {
            font-weight: 900;
            font-size: 1.2rem;
            letter-spacing: -0.05em;
            text-transform: uppercase;
            color: var(--text-main);
            opacity: 0.8;
        }

        /* Debossed Relief Effect */
        .card-debossed {
            background: var(--paper-bg);
            border-radius: 32px;
            padding: 32px;
            box-shadow: 
                inset 8px 8px 16px var(--shadow-dark), 
                inset -8px -8px 16px var(--shadow-light);
            margin-bottom: 32px;
            position: relative;
        }

        .balance-label {
            font-size: 0.75rem;
            font-weight: 700;
            text-transform: uppercase;
            letter-spacing: 0.1em;
            color: var(--text-muted);
            margin-bottom: 8px;
            display: block;
        }

        .balance-amount {
            font-family: 'JetBrains Mono', monospace;
            font-size: 2.5rem;
            font-weight: 500;
            letter-spacing: -0.02em;
            color: var(--text-main);
        }

        .grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
            margin-bottom: 40px;
        }

        /* Raised Tile Effect */
        .tile {
            aspect-ratio: 1 / 1;
            background: var(--paper-bg);
            border-radius: 24px;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            padding: 20px;
            text-decoration: none;
            color: inherit;
            box-shadow: 
                8px 8px 16px var(--shadow-dark), 
                -8px -8px 16px var(--shadow-light);
            transition: all 0.2s ease;
            border: 1px solid rgba(255,255,255,0.1);
        }

        .tile:active {
            box-shadow: 
                inset 4px 4px 8px var(--shadow-dark), 
                inset -4px -4px 8px var(--shadow-light);
            transform: scale(0.98);
        }

        .tile-icon {
            width: 36px;
            height: 36px;
            border-radius: 10px;
            display: flex;
            align-items: center;
            justify-content: center;
            box-shadow: 
                inset 2px 2px 4px var(--shadow-dark), 
                inset -2px -2px 4px var(--shadow-light);
        }

        .tile-label {
            font-weight: 700;
            font-size: 0.9rem;
            letter-spacing: -0.01em;
        }

        .section-header {
            display: flex;
            justify-content: space-between;
            align-items: flex-end;
            margin-bottom: 20px;
        }

        .section-title {
            font-weight: 900;
            font-size: 1.1rem;
            text-transform: uppercase;
            letter-spacing: 0.05em;
        }

        .see-all {
            font-size: 0.75rem;
            font-weight: 700;
            color: var(--text-muted);
            text-decoration: none;
            padding: 4px 8px;
            border-radius: 8px;
            box-shadow: 2px 2px 4px var(--shadow-dark), -2px -2px 4px var(--shadow-light);
        }

        .transaction-list {
            display: flex;
            flex-direction: column;
            gap: 12px;
        }

        .transaction-item {
            display: flex;
            align-items: center;
            padding: 16px;
            border-radius: 16px;
            box-shadow: 
                inset 3px 3px 6px var(--shadow-dark), 
                inset -3px -3px 6px var(--shadow-light);
        }

        .tx-info {
            flex-grow: 1;
        }

        .tx-name {
            font-weight: 700;
            font-size: 0.95rem;
            display: block;
        }

        .tx-date {
            font-size: 0.75rem;
            color: var(--text-muted);
        }

        .tx-amount {
            font-family: 'JetBrains Mono', monospace;
            font-weight: 500;
            font-size: 0.95rem;
        }

        .tx-amount.neg { color: var(--negative); }
        .tx-amount.pos { color: var(--positive); }

        /* Floating Action Button */
        .fab {
            position: fixed;
            bottom: 40px;
            right: 24px;
            width: 64px;
            height: 64px;
            background: var(--paper-bg);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 2rem;
            font-weight: 300;
            color: var(--text-main);
            box-shadow: 
                12px 12px 24px var(--shadow-dark), 
                -12px -12px 24px var(--shadow-light);
            cursor: pointer;
            border: none;
            transition: transform 0.2s cubic-bezier(0.175, 0.885, 0.32, 1.275);
            z-index: 50;
        }

        .fab:hover {
            transform: scale(1.1) rotate(90deg);
        }

        .fab:active {
            box-shadow: 
                inset 6px 6px 12px var(--shadow-dark), 
                inset -6px -6px 12px var(--shadow-light);
            transform: scale(0.95);
        }

        svg.grain-filter {
            position: absolute;
            width: 0;
            height: 0;
        }
    </style>
</head>
<body>

    <svg class="grain-filter">
        <filter id="grain">
            <feTurbulence type="fractalNoise" baseFrequency="0.65" numOctaves="3" stitchTiles="stitch" />
            <feColorMatrix type="saturate" values="0" />
        </filter>
    </svg>

    <div class="app-container">
        <header>
            <div class="logo">Ledger</div>
        </header>

        <section class="card-debossed">
            <span class="balance-label">Current Balance</span>
            <div class="balance-amount">$14,280.50</div>
        </section>

        <section class="grid">
            <a href="#" class="tile">
                <div class="tile-icon">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M21 2v6h-6"></path><path d="M3 12a9 9 0 0 1 15-6.7L21 8"></path><path d="M3 22v-6h6"></path><path d="M21 12a9 9 0 0 1-15 6.7L3 16"></path></svg>
                </div>
                <span class="tile-label">Recurring</span>
            </a>
            <a href="#" class="tile">
                <div class="tile-icon">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M21.21 15.89A10 10 0 1 1 8 2.83"></path><path d="M22 12A10 10 0 0 0 12 2v10z"></path></svg>
                </div>
                <span class="tile-label">Summary</span>
            </a>
            <a href="#" class="tile">
                <div class="tile-icon">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="7" width="20" height="14" rx="2" ry="2"></rect><path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"></path></svg>
                </div>
                <span class="tile-label">Budgets</span>
            </a>
            <a href="#" class="tile">
                <div class="tile-icon">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M17.5 19a3.5 3.5 0 0 0 0-7h-1.5c.1-3.6-2.5-6.5-6-6.5a6.5 6.5 0 0 0-6.1 4.5 4.5 4.5 0 0 0 .6 8.5"></path><polyline points="12 13 12 17"></polyline><polyline points="9 14 12 17 15 14"></polyline></svg>
                </div>
                <span class="tile-label">Cloud Sync</span>
            </a>
        </section>

        <section class="recent-transactions">
            <div class="section-header">
                <h2 class="section-title">Transactions</h2>
                <a href="#" class="see-all">See All</a>
            </div>
            
            <div class="transaction-list">
                <div class="transaction-item">
                    <div class="tx-info">
                        <span class="tx-name">Coffee Roast Co.</span>
                        <span class="tx-date">Today, 10:24 AM</span>
                    </div>
                    <div class="tx-amount neg">-$6.50</div>
                </div>
                <div class="transaction-item">
                    <div class="tx-info">
                        <span class="tx-name">Employer Transfer</span>
                        <span class="tx-date">Yesterday</span>
                    </div>
                    <div class="tx-amount pos">+$3,200.00</div>
                </div>
                <div class="transaction-item">
                    <div class="tx-info">
                        <span class="tx-name">Grocery Mart</span>
                        <span class="tx-date">Oct 24, 2023</span>
                    </div>
                    <div class="tx-amount neg">-$142.10</div>
                </div>
                <div class="transaction-item">
                    <div class="tx-info">
                        <span class="tx-name">Digital Subscription</span>
                        <span class="tx-date">Oct 22, 2023</span>
                    </div>
                    <div class="tx-amount neg">-$14.99</div>
                </div>
            </div>
        </section>
    </div>

    <button class="fab" aria-label="Add Transaction">+</button>

    <script>
        // Subtle interaction: adding a parallax-ish shadow shift on mouse move
        const app = document.querySelector('.app-container');
        document.addEventListener('mousemove', (e) => {
            const x = (window.innerWidth / 2 - e.pageX) / 50;
            const y = (window.innerHeight / 2 - e.pageY) / 50;
            // Apply slight tilt to cards for hyper-realism
            const cards = document.querySelectorAll('.tile, .card-debossed, .fab');
            cards.forEach(card => {
                // card.style.transform = `rotateY(${x}deg) rotateX(${-y}deg)`;
            });
        });

        // Click interaction for FAB
        document.querySelector('.fab').addEventListener('click', () => {
            console.log('Open Add Transaction Modal');
        });
    </script>
</body>
</html>

```