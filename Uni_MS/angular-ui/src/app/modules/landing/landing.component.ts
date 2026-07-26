import { Component, OnInit, OnDestroy, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './landing.component.html',
  styles: [`
    :host {
      --bg-base: #f8fafc;
      --bg-alt: #f1f5f9;
      --bg-topbar: rgba(255, 255, 255, 0.85);
      --bg-dash: rgba(255, 255, 255, 0.9);
      --bg-dash-solid: rgba(255, 255, 255, 0.95);
      --text-primary: #0f172a;
      --text-secondary: #334155;
      --text-muted: #64748b;
      --text-muted-dark: #475569;
      --accent-color: #0056b3;
      --brand-color: #002d5f;
      --border-ultralight: rgba(0, 0, 0, 0.02);
      --border-verylight: rgba(0, 0, 0, 0.03);
      --border-light: rgba(0, 0, 0, 0.04);
      --border-medium: rgba(0, 0, 0, 0.05);
      --border-heavy: rgba(0, 0, 0, 0.06);
      --border-strong: rgba(0, 0, 0, 0.08);
      --border-stronger: rgba(0, 0, 0, 0.1);
      --border-visible: rgba(0, 0, 0, 0.15);
    }

    :host-context(.dark-theme) {
      --bg-base: #0a1628;
      --bg-alt: #0f1f3a;
      --bg-topbar: rgba(10, 22, 40, 0.85);
      --bg-dash: rgba(15, 25, 50, 0.6);
      --bg-dash-solid: rgba(15, 25, 50, 0.8);
      --text-primary: #e8edf5;
      --text-secondary: #b0bec5;
      --text-muted: #78909c;
      --text-muted-dark: #546e7a;
      --accent-color: #5b9bd5;
      --brand-color: #002d5f;
      --border-ultralight: rgba(255, 255, 255, 0.02);
      --border-verylight: rgba(255, 255, 255, 0.03);
      --border-light: rgba(255, 255, 255, 0.04);
      --border-medium: rgba(255, 255, 255, 0.05);
      --border-heavy: rgba(255, 255, 255, 0.06);
      --border-strong: rgba(255, 255, 255, 0.08);
      --border-stronger: rgba(255, 255, 255, 0.1);
      --border-visible: rgba(255, 255, 255, 0.15);
    }


    :host { display: block; }

    :host ::ng-deep * {
      margin: 0;
      padding: 0;
      box-sizing: border-box;
    }

    /* ═══════════════════════════════════════
         SMART TOP BAR
    ═══════════════════════════════════════ */
    .topbar {
      position: fixed;
      top: 0;
      left: 0;
      right: 0;
      z-index: 100;
      padding: 0 2rem;
      transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
    }

    .topbar.scrolled {
      background: var(--bg-topbar);
      backdrop-filter: blur(20px) saturate(1.8);
      -webkit-backdrop-filter: blur(20px) saturate(1.8);
      border-bottom: 1px solid var(--border-heavy);
    }

    .topbar-inner {
      max-width: 1280px;
      margin: 0 auto;
      display: flex;
      align-items: center;
      justify-content: space-between;
      height: 72px;
    }

    .topbar-brand {
      display: flex;
      align-items: center;
      gap: 10px;
      text-decoration: none;
    }

    .brand-name {
      font-size: 1.25rem;
      font-weight: 700;
      color: var(--text-primary);
      letter-spacing: -0.5px;
    }

    .brand-name span {
      color: #8bb8e0;
    }

    .topbar-nav {
      display: flex;
      align-items: center;
      gap: 2px;
    }

    .topbar-nav a {
      color: var(--text-muted);
      text-decoration: none;
      font-size: 0.875rem;
      font-weight: 500;
      padding: 8px 16px;
      border-radius: 8px;
      transition: all 0.2s ease;
      position: relative;
    }

    .topbar-nav a:hover {
      color: var(--text-secondary);
      background: var(--border-medium);
    }

    .topbar-actions {
      display: flex;
      align-items: center;
      gap: 12px;
    }

    .btn-ghost {
      color: var(--text-muted);
      text-decoration: none;
      font-size: 0.875rem;
      font-weight: 500;
      padding: 8px 16px;
      border-radius: 8px;
      transition: all 0.2s ease;
    }

    .btn-ghost:hover {
      color: var(--text-secondary);
      background: var(--border-medium);
    }

    .btn-primary {
      display: inline-flex;
      align-items: center;
      gap: 8px;
      padding: 10px 20px;
      background: linear-gradient(135deg, #10b981, #059669);
      color: #fff;
      border: none;
      border-radius: 10px;
      font-size: 0.875rem;
      font-weight: 600;
      text-decoration: none;
      cursor: pointer;
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      box-shadow: 0 0 20px rgba(16, 185, 129, 0.3), inset 0 1px 0 rgba(255, 255, 255, 0.15);
      border: 1px solid rgba(16, 185, 129, 0.5);
    }

    .btn-primary:hover {
      transform: translateY(-1px);
      box-shadow: 0 0 30px rgba(16, 185, 129, 0.5), inset 0 1px 0 rgba(255, 255, 255, 0.2);
    }

    .btn-primary svg {
      width: 16px;
      height: 16px;
    }

    .theme-toggle-btn {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      border: 1px solid var(--border-stronger);
      background: var(--border-medium);
      backdrop-filter: blur(10px);
      -webkit-backdrop-filter: blur(10px);
      display: flex;
      align-items: center;
      justify-content: center;
      color: var(--text-muted);
      cursor: pointer;
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      position: relative;
      overflow: hidden;
    }

    .theme-toggle-btn:hover {
      color: var(--text-secondary);
      background: var(--border-stronger);
      transform: scale(1.05);
    }

    .theme-toggle-btn:active {
      transform: scale(0.95);
    }

    .theme-toggle-btn .material-symbols-outlined {
      font-size: 22px;
      transition: transform 0.3s ease;
    }

    /* ═══════════════════════════════════════
         HERO SECTION
    ═══════════════════════════════════════ */
    .hero {
      min-height: 100vh;
      display: flex;
      align-items: center;
      position: relative;
      overflow: hidden;
      padding: 120px 2rem 80px;
      background: var(--bg-base);
    }

    .hero-grid-bg {
      position: absolute;
      inset: 0;
      background-image:
        linear-gradient(var(--border-ultralight) 1px, transparent 1px),
        linear-gradient(90deg, var(--border-ultralight) 1px, transparent 1px);
      background-size: 80px 80px;
      mask-image: radial-gradient(ellipse at center, black 30%, transparent 70%);
      -webkit-mask-image: radial-gradient(ellipse at center, black 30%, transparent 70%);
    }

    .hero-glow {
      display: none;
    }

    .hero-glow-1 {
      top: -200px;
      left: -100px;
      background: #002d5f;
    }

    .hero-glow-2 {
      bottom: -200px;
      right: -100px;
      background: #0056b3;
    }

    .hero-content {
      position: relative;
      z-index: 2;
      max-width: 1280px;
      margin: 0 auto;
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 4rem;
      align-items: center;
      width: 100%;
    }

    .hero-badge {
      display: inline-flex;
      align-items: center;
      gap: 8px;
      padding: 6px 14px 6px 6px;
      background: rgba(0, 45, 95, 0.1);
      border: 1px solid rgba(0, 45, 95, 0.2);
      border-radius: 100px;
      font-size: 0.8125rem;
      color: #002d5f;
      font-weight: 500;
      margin-bottom: 1.5rem;
    }

    .hero-badge-dot {
      width: 24px;
      height: 24px;
      border-radius: 50%;
      background: linear-gradient(135deg, #002d5f, #0056b3);
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .hero-badge-dot svg {
      width: 12px;
      height: 12px;
      color: #fff;
    }

    .hero-title {
      font-size: 4rem;
      font-weight: 800;
      color: var(--text-primary);
      line-height: 1.05;
      letter-spacing: -2px;
      margin-bottom: 1.5rem;
    }

    .hero-title .gradient {
      background: linear-gradient(135deg, #002d5f 0%, #0056b3 50%, #5b9bd5 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }

    .hero-subtitle {
      font-size: 1.125rem;
      color: var(--text-muted-dark);
      line-height: 1.7;
      max-width: 520px;
      margin-bottom: 2.5rem;
    }

    .hero-actions {
      display: flex;
      gap: 12px;
      align-items: center;
    }

    .btn-hero-primary {
      display: inline-flex;
      align-items: center;
      gap: 10px;
      padding: 14px 28px;
      background: linear-gradient(135deg, #10b981, #059669);
      color: #fff;
      border: none;
      border-radius: 12px;
      font-size: 0.9375rem;
      font-weight: 600;
      text-decoration: none;
      cursor: pointer;
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      box-shadow: 0 4px 15px rgba(16, 185, 129, 0.4), 0 0 0 1px rgba(16, 185, 129, 0.3);
    }

    .btn-hero-primary:hover {
      transform: translateY(-2px);
      box-shadow: 0 8px 25px rgba(16, 185, 129, 0.5), 0 0 0 1px rgba(16, 185, 129, 0.4);
    }

    .btn-hero-primary svg {
      width: 18px;
      height: 18px;
    }

    .btn-hero-secondary {
      display: inline-flex;
      align-items: center;
      gap: 10px;
      padding: 14px 28px;
      background: transparent;
      color: var(--text-muted);
      border: 1px solid var(--border-stronger);
      border-radius: 12px;
      font-size: 0.9375rem;
      font-weight: 500;
      text-decoration: none;
      cursor: pointer;
      transition: all 0.3s ease;
    }

    .btn-hero-secondary:hover {
      color: var(--text-secondary);
      border-color: var(--border-visible);
      background: var(--border-verylight);
    }

    .btn-hero-secondary svg {
      width: 18px;
      height: 18px;
    }

    .hero-proof {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-top: 2.5rem;
      padding-top: 2rem;
      border-top: 1px solid var(--border-light);
    }

    .hero-proof-avatars {
      display: flex;
      margin-right: -4px;
    }

    .avatar {
      width: 32px;
      height: 32px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 0.6875rem;
      font-weight: 700;
      color: #fff;
      border: 2px solid var(--bg-base);
      margin-left: -8px;
    }

    .avatar:first-child {
      margin-left: 0;
    }

    .hero-proof-text {
      font-size: 0.8125rem;
      color: var(--text-muted);
      font-weight: 500;
    }

    /* Hero Visual */
    .hero-visual {
      position: relative;
    }

    .hero-dashboard {
      background: var(--bg-dash);
      border: 1px solid var(--border-strong);
      border-radius: 20px;
      padding: 24px;
      backdrop-filter: blur(20px);
      box-shadow: 0 40px 80px rgba(0, 0, 0, 0.12), 0 0 0 1px var(--border-medium);
    }

    .dash-topbar {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 20px;
      padding-bottom: 16px;
      border-bottom: 1px solid var(--border-heavy);
    }

    .dash-dots {
      display: flex;
      gap: 6px;
    }

    .dash-dots span {
      width: 10px;
      height: 10px;
      border-radius: 50%;
    }

    .dash-dots span:nth-child(1) { background: #dc3545; }
    .dash-dots span:nth-child(2) { background: #eab308; }
    .dash-dots span:nth-child(3) { background: #28a745; }

    .dash-nav {
      display: flex;
      gap: 4px;
    }

    .dash-nav-item {
      padding: 4px 10px;
      border-radius: 6px;
      font-size: 0.6875rem;
      color: var(--text-muted-dark);
      font-weight: 500;
    }

    .dash-nav-item.active {
      background: rgba(0, 45, 95, 0.1);
      color: #002d5f;
    }

    .dash-stats {
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      gap: 12px;
      margin-bottom: 16px;
    }

    .dash-stat {
      background: var(--border-verylight);
      border: 1px solid var(--border-medium);
      border-radius: 12px;
      padding: 14px;
    }

    .dash-stat-label {
      font-size: 0.6875rem;
      color: var(--text-muted-dark);
      text-transform: uppercase;
      letter-spacing: 0.5px;
      margin-bottom: 4px;
    }

    .dash-stat-value {
      font-size: 1.375rem;
      font-weight: 700;
    }

    .dash-stat-change {
      font-size: 0.6875rem;
      margin-top: 4px;
      display: flex;
      align-items: center;
      gap: 4px;
    }

    .dash-stat-change.up { color: #34d399; }
    .dash-stat-change.down { color: #f87171; }

    .dash-chart {
      background: var(--border-ultralight);
      border: 1px solid var(--border-light);
      border-radius: 12px;
      padding: 16px;
    }

    .dash-chart-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 12px;
    }

    .dash-chart-title {
      font-size: 0.75rem;
      color: var(--text-muted);
      font-weight: 600;
    }

    .dash-chart-legend {
      display: flex;
      gap: 12px;
    }

    .legend-dot {
      display: flex;
      align-items: center;
      gap: 4px;
      font-size: 0.625rem;
      color: var(--text-muted-dark);
    }

    .legend-dot::before {
      content: '';
      width: 6px;
      height: 6px;
      border-radius: 50%;
    }

    .legend-dot.primary::before { background: #002d5f; }
    .legend-dot.secondary::before { background: #a855f7; }

    .dash-bars {
      display: flex;
      align-items: flex-end;
      gap: 5px;
      height: 100px;
    }

    .dash-bar {
      flex: 1;
      border-radius: 4px 4px 0 0;
      transition: all 0.3s ease;
      position: relative;
    }

    .dash-bar:hover {
      opacity: 0.8;
    }

    /* Floating elements */
    .float-card {
      position: absolute;
      background: var(--bg-dash-solid);
      border: 1px solid var(--border-stronger);
      border-radius: 12px;
      padding: 12px 16px;
      backdrop-filter: blur(16px);
      box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
      animation: floatY 4s ease-in-out infinite;
    }

    .float-card-1 {
      top: 40px;
      right: -30px;
      animation-delay: 0s;
    }

    .float-card-2 {
      bottom: 60px;
      left: -40px;
      animation-delay: 2s;
    }

    .float-card-inner {
      display: flex;
      align-items: center;
      gap: 10px;
    }

    .float-icon {
      width: 32px;
      height: 32px;
      border-radius: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .float-icon svg {
      width: 16px;
      height: 16px;
    }

    .float-icon.green {
      background: rgba(16, 185, 129, 0.15);
      color: #10b981;
    }

    .float-icon.blue {
      background: rgba(0, 86, 179, 0.15);
      color: #0056b3;
    }

    .float-text {
      font-size: 0.75rem;
      color: var(--text-secondary);
      font-weight: 600;
    }

    .float-sub {
      font-size: 0.625rem;
      color: var(--text-muted-dark);
    }

    @keyframes floatY {
      0%, 100% { transform: translateY(0); }
      50% { transform: translateY(-10px); }
    }

    /* ═══════════════════════════════════════
         TRUSTED BY
    ═══════════════════════════════════════ */
    .trusted {
      padding: 4rem 2rem;
      text-align: center;
      background: var(--bg-base);
      border-top: 1px solid var(--border-light);
    }

    .trusted-label {
      font-size: 0.8125rem;
      color: var(--text-muted);
      text-transform: uppercase;
      letter-spacing: 2px;
      font-weight: 600;
      margin-bottom: 2rem;
    }

    .trusted-logos {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 3rem;
      flex-wrap: wrap;
      opacity: 0.4;
    }

    .trusted-logo {
      font-size: 1.125rem;
      font-weight: 700;
      color: var(--text-muted-dark);
      letter-spacing: -0.5px;
    }

    /* ═══════════════════════════════════════
         FEATURES
    ═══════════════════════════════════════ */
    .features-section {
      padding: 8rem 2rem;
      background: var(--bg-base);
      position: relative;
    }

    .section-container {
      max-width: 1280px;
      margin: 0 auto;
    }

    .section-header {
      text-align: center;
      max-width: 640px;
      margin: 0 auto 4rem;
    }

    .section-tag {
      display: inline-flex;
      align-items: center;
      gap: 6px;
      padding: 6px 14px;
      background: rgba(0, 45, 95, 0.08);
      border: 1px solid rgba(0, 45, 95, 0.12);
      border-radius: 100px;
      font-size: 0.8125rem;
      font-weight: 600;
      color: #002d5f;
      text-transform: uppercase;
      letter-spacing: 1px;
      margin-bottom: 1.25rem;
    }

    .section-title {
      font-size: 2.75rem;
      font-weight: 800;
      color: var(--text-primary);
      line-height: 1.15;
      letter-spacing: -1px;
      margin-bottom: 1rem;
    }

    .section-desc {
      font-size: 1.0625rem;
      color: var(--text-muted-dark);
      line-height: 1.7;
    }

    .features-grid {
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      gap: 16px;
    }

    .feature-card {
      background: var(--bg-base);
      border: 1px solid var(--border-heavy);
      border-radius: 16px;
      padding: 28px;
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      position: relative;
      overflow: hidden;
      cursor: default;
    }

    .feature-card::before {
      content: '';
      position: absolute;
      inset: 0;
      border-radius: 16px;
      padding: 1px;
      background: linear-gradient(135deg, transparent 40%, var(--accent) 100%);
      -webkit-mask: linear-gradient(var(--text-primary) 0 0) content-box, linear-gradient(var(--text-primary) 0 0);
      -webkit-mask-composite: xor;
      mask-composite: exclude;
      opacity: 0;
      transition: opacity 0.3s ease;
      pointer-events: none;
    }

    .feature-card:hover {
      background: var(--border-verylight);
      transform: translateY(-4px);
      box-shadow: 0 20px 40px -12px rgba(0, 0, 0, 0.08);
    }

    .feature-card:hover::before {
      opacity: 1;
    }

    .feature-icon-wrap {
      width: 48px;
      height: 48px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-bottom: 1.25rem;
    }

    .feature-icon-wrap svg {
      width: 22px;
      height: 22px;
    }

    .feature-card h3 {
      font-size: 1rem;
      font-weight: 700;
      color: var(--text-primary);
      margin-bottom: 0.5rem;
    }

    .feature-card p {
      font-size: 0.8125rem;
      color: var(--text-muted);
      line-height: 1.65;
    }

    /* ═══════════════════════════════════════
         HOW IT WORKS
    ═══════════════════════════════════════ */
    .how-section {
      padding: 8rem 2rem;
      background: linear-gradient(180deg, var(--bg-base) 0%, var(--bg-alt) 100%);
    }

    .steps-grid {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 24px;
      position: relative;
    }

    .steps-grid::before {
      content: '';
      position: absolute;
      top: 36px;
      left: 12%;
      right: 12%;
      height: 2px;
      background: linear-gradient(90deg, transparent, rgba(0, 45, 95, 0.2), rgba(91, 155, 213, 0.2), transparent);
    }

    .step-card {
      text-align: center;
      position: relative;
    }

    .step-number {
      width: 72px;
      height: 72px;
      border-radius: 50%;
      background: rgba(0, 45, 95, 0.08);
      border: 2px solid rgba(0, 45, 95, 0.15);
      display: flex;
      align-items: center;
      justify-content: center;
      margin: 0 auto 1.5rem;
      font-size: 1.5rem;
      font-weight: 800;
      color: #002d5f;
      position: relative;
      z-index: 2;
    }

    .step-card h3 {
      font-size: 1rem;
      font-weight: 700;
      color: var(--text-primary);
      margin-bottom: 0.5rem;
    }

    .step-card p {
      font-size: 0.8125rem;
      color: var(--text-muted);
      line-height: 1.6;
      max-width: 240px;
      margin: 0 auto;
    }

    /* ═══════════════════════════════════════
         MODULES
    ═══════════════════════════════════════ */
    .modules-section {
      padding: 6rem 2rem;
      background: var(--bg-alt);
    }

    .modules-grid {
      max-width: 1200px;
      margin: 3rem auto 0;
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      gap: 1rem;
    }

    .module-card {
      display: flex;
      align-items: flex-start;
      gap: 1rem;
      padding: 1.25rem;
      background: var(--bg-base);
      border: 1px solid var(--border-light);
      border-radius: 12px;
      text-decoration: none;
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      cursor: pointer;
    }

    .module-card:hover {
      border-color: var(--mod-color, #002d5f);
      box-shadow: 0 8px 24px -8px rgba(0,0,0,0.12), 0 0 0 1px var(--mod-color, #002d5f);
      transform: translateY(-2px);
    }

    .module-icon {
      width: 44px;
      height: 44px;
      border-radius: 10px;
      display: flex;
      align-items: center;
      justify-content: center;
      flex-shrink: 0;
    }

    .module-icon .material-symbols-outlined {
      font-size: 22px;
    }

    .module-info {
      flex: 1;
      min-width: 0;
    }

    .module-info h3 {
      font-size: 0.9375rem;
      font-weight: 600;
      color: var(--text-primary);
      margin-bottom: 4px;
    }

    .module-info p {
      font-size: 0.75rem;
      color: var(--text-muted);
      line-height: 1.5;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    .module-meta {
      display: flex;
      flex-direction: column;
      align-items: flex-end;
      gap: 4px;
      flex-shrink: 0;
    }

    .module-count {
      font-size: 0.6875rem;
      color: var(--text-muted);
      white-space: nowrap;
    }

    .module-meta svg {
      color: var(--text-muted);
      transition: transform 0.2s;
    }

    .module-card:hover .module-meta svg {
      transform: translateX(3px);
      color: var(--mod-color, #002d5f);
    }

    /* ═══════════════════════════════════════
         STATS
    ═══════════════════════════════════════ */
    .stats-section {
      padding: 6rem 2rem;
      background: var(--bg-base);
      border-top: 1px solid var(--border-light);
      border-bottom: 1px solid var(--border-light);
    }

    .stats-row {
      max-width: 1000px;
      margin: 0 auto;
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 2rem;
    }

    .stat-block {
      text-align: center;
    }

    .stat-value {
      font-size: 3rem;
      font-weight: 800;
      letter-spacing: -1px;
      line-height: 1;
      margin-bottom: 0.5rem;
      background: linear-gradient(135deg, #002d5f, #0056b3);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }

    .stat-label {
      font-size: 0.875rem;
      color: var(--text-muted-dark);
      font-weight: 500;
    }

    /* ═══════════════════════════════════════
         CTA
    ═══════════════════════════════════════ */
    .cta-section {
      padding: 8rem 2rem;
      background: var(--bg-base);
      text-align: center;
    }

    .cta-container {
      max-width: 720px;
      margin: 0 auto;
      position: relative;
    }

    .cta-glow {
      position: absolute;
      inset: -40px;
      background: radial-gradient(ellipse at center, rgba(0, 45, 95, 0.1), transparent 70%);
      pointer-events: none;
    }

    .cta-box {
      position: relative;
      background: var(--bg-dash);
      border: 1px solid var(--border-strong);
      border-radius: 24px;
      padding: 4rem 3rem;
      backdrop-filter: blur(20px);
    }

    .cta-title {
      font-size: 2.25rem;
      font-weight: 800;
      color: var(--text-primary);
      letter-spacing: -1px;
      margin-bottom: 1rem;
    }

    .cta-desc {
      font-size: 1.0625rem;
      color: var(--text-muted-dark);
      line-height: 1.7;
      margin-bottom: 2.5rem;
      max-width: 500px;
      margin-left: auto;
      margin-right: auto;
    }

    .cta-demo-grid {
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      gap: 12px;
      max-width: 500px;
      margin: 0 auto 2.5rem;
    }

    .demo-item {
      background: var(--border-verylight);
      border: 1px solid var(--border-heavy);
      border-radius: 10px;
      padding: 12px;
      text-align: center;
    }

    .demo-item-role {
      font-size: 0.75rem;
      font-weight: 700;
      color: #002d5f;
      margin-bottom: 4px;
    }

    .demo-item-creds {
      font-size: 0.6875rem;
      color: var(--text-muted-dark);
      font-family: 'SF Mono', 'Fira Code', monospace;
    }

    /* ═══════════════════════════════════════
         FOOTER
    ═══════════════════════════════════════ */
    .footer {
      padding: 4rem 2rem 2rem;
      background: var(--bg-base);
      border-top: 1px solid var(--border-light);
    }

    .footer-inner {
      max-width: 1280px;
      margin: 0 auto;
    }

    .footer-grid {
      display: grid;
      grid-template-columns: 2fr 1fr 1fr 1fr;
      gap: 3rem;
      margin-bottom: 3rem;
    }

    .footer-brand p {
      font-size: 0.875rem;
      color: var(--text-muted);
      line-height: 1.6;
      margin-top: 1rem;
      max-width: 280px;
    }

    .footer-col h4 {
      font-size: 0.8125rem;
      font-weight: 700;
      color: var(--text-muted);
      text-transform: uppercase;
      letter-spacing: 1px;
      margin-bottom: 1rem;
    }

    .footer-col a {
      display: block;
      font-size: 0.875rem;
      color: var(--text-muted);
      text-decoration: none;
      padding: 4px 0;
      transition: color 0.2s ease;
    }

    .footer-col a:hover {
      color: var(--text-secondary);
    }

    .footer-bottom {
      padding-top: 2rem;
      border-top: 1px solid var(--border-light);
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .footer-bottom p {
      font-size: 0.8125rem;
      color: var(--text-muted-dark);
    }

    .footer-bottom-links {
      display: flex;
      gap: 1.5rem;
    }

    .footer-bottom-links a {
      font-size: 0.8125rem;
      color: var(--text-muted-dark);
      text-decoration: none;
      transition: color 0.2s ease;
    }

    .footer-bottom-links a:hover {
      color: var(--text-secondary);
    }

    /* ═══════════════════════════════════════
         RESPONSIVE
    ═══════════════════════════════════════ */
    @media (max-width: 1024px) {
      .hero-content { grid-template-columns: 1fr; text-align: center; }
      .hero-subtitle { margin-left: auto; margin-right: auto; }
      .hero-actions { justify-content: center; }
      .hero-proof { justify-content: center; }
      .hero-visual { max-width: 560px; margin: 3rem auto 0; }
      .features-grid { grid-template-columns: repeat(2, 1fr); }
      .modules-grid { grid-template-columns: repeat(2, 1fr); }
      .steps-grid { grid-template-columns: repeat(2, 1fr); }
      .steps-grid::before { display: none; }
      .footer-grid { grid-template-columns: repeat(2, 1fr); }
      .float-card { display: none; }
    }

    @media (max-width: 640px) {
      .topbar-nav { display: none; }
      .hero-title { font-size: 2.5rem; letter-spacing: -1px; }
      .section-title { font-size: 2rem; }
      .features-grid { grid-template-columns: 1fr; }
      .modules-grid { grid-template-columns: 1fr; }
      .steps-grid { grid-template-columns: 1fr; }
      .stats-row { grid-template-columns: repeat(2, 1fr); }
      .cta-box { padding: 2.5rem 1.5rem; }
      .cta-demo-grid { grid-template-columns: 1fr; }
      .footer-grid { grid-template-columns: 1fr; }
      .footer-bottom { flex-direction: column; gap: 1rem; text-align: center; }
    }
  `]
})
export class LandingComponent implements OnInit, OnDestroy {
  features = [
    { icon: 'shield', title: 'Role-Based Access Control', desc: 'Granular permissions with 50+ controls, 8 role templates, and dynamic menu assignment.', color: '#002d5f' },
    { icon: 'school', title: 'Academic Management', desc: 'Departments, courses, batches, sections, subjects, and course assignments.', color: '#5a3e8e' },
    { icon: 'person_add', title: 'Admissions Pipeline', desc: 'End-to-end admission with test scoring, academic results, and document verification.', color: '#0056b3' },
    { icon: 'groups', title: 'Student Lifecycle', desc: 'Enrollment, profiles, guardians, and complete student records management.', color: '#34d399' },
    { icon: 'badge', title: 'Administration Management', desc: 'Administrative staff, officer profiles, and department management.', color: '#5b9bd5' },
    { icon: 'business', title: 'HR & Payroll', desc: 'Employee records, leave management, attendance, and payroll processing.', color: '#fbbf24' },
    { icon: 'quiz', title: 'Examination System', desc: 'Exam scheduling, marks entry, grade rules, and result publishing.', color: '#c8102e' },
    { icon: 'menu_book', title: 'Learning Management', desc: 'Assignments, submissions, course materials, and online class scheduling.', color: '#fb923c' },
    { icon: 'payments', title: 'Finance & Invoicing', desc: 'Fee types, student fees, invoices, payments, accounts, and transactions.', color: '#a3e635' },
    { icon: 'local_library', title: 'Library System', desc: 'Book catalog, categories, issue tracking, and return management.', color: '#38bdf8' },
    { icon: 'apartment', title: 'Hostel Management', desc: 'Hostels, rooms, allocations, and occupancy tracking.', color: '#5b9bd5' },
    { icon: 'directions_bus', title: 'Transport', desc: 'Vehicles, routes, and transport allocation management.', color: '#fb7185' },
    { icon: 'mail', title: 'Communication Hub', desc: 'Notices, announcements, messages, and notification delivery.', color: '#4ade80' },
    { icon: 'emoji_events', title: 'Activities & Events', desc: 'Clubs, sports, events, and registration management.', color: '#e879f9' },
    { icon: 'assessment', title: 'Reports & Analytics', desc: 'Custom report templates and generated analytics dashboards.', color: '#facc15' }
  ];
  stats = [
    { value: '15', label: 'Modules' },
    { value: '63', label: 'Database Tables' },
    { value: '50+', label: 'Permission Controls' },
    { value: '8', label: 'Role Templates' },
  ];

  steps = [
    { num: '01', title: 'Configure Roles', desc: 'Super Admin creates roles and assigns permissions from the admin panel.' },
    { num: '02', title: 'Assign Menus', desc: 'Dynamic menu tree built from database ΓÇö each role sees only what matters.' },
    { num: '03', title: 'Manage Data', desc: 'Server-side pagination, sorting, and filtering across all 63 entities.' },
    { num: '04', title: 'Track Activity', desc: 'Full audit logging captures every create, update, and delete operation.' },
  ];

  modules = [
    { icon: 'school', name: 'Academic', desc: 'Faculties, departments, courses, sessions, semesters, batches, sections, subjects, curriculum, credit rules, prerequisites, and class routines.', color: '#002d5f', count: '18 sub-modules' },
    { icon: 'assignment', name: 'Admissions', desc: 'Sessions, circulars, applications, test scoring, merit lists, interviews, document verification, enrollment, and student ID generation.', color: '#5a3e8e', count: '20 sub-modules' },
    { icon: 'person', name: 'Students', desc: 'Student list, enrollments, profiles, guardians, attendance, academic history, course registration, results, transcripts, and certificates.', color: '#34d399', count: '17 sub-modules' },
    { icon: 'person_add', name: 'Administration', desc: 'Administrative heads, academic heads, head of offices, and staff management across all divisions.', color: '#5b9bd5', count: '4 sub-modules' },
    { icon: 'business', name: 'HRM', desc: 'Employee management, attendance tracking, leave requests, and payroll processing.', color: '#fbbf24', count: '4 sub-modules' },
    { icon: 'quiz', name: 'Examination', desc: 'Exam scheduling, marks entry, grade rules, result publishing, and academic performance tracking.', color: '#c8102e', count: '5 sub-modules' },
    { icon: 'menu_book', name: 'Learning Management', desc: 'Assignments, submissions, course materials, and online class scheduling.', color: '#fb923c', count: '4 sub-modules' },
    { icon: 'payments', name: 'Finance', desc: 'Fee types, student fees, invoices, payments, accounts, and transaction management.', color: '#a3e635', count: '6 sub-modules' },
    { icon: 'local_library', name: 'Library', desc: 'Book catalog, categories, issue tracking, and return management.', color: '#38bdf8', count: '4 sub-modules' },
    { icon: 'apartment', name: 'Hostel', desc: 'Hostels, rooms, allocations, and occupancy tracking for campus housing.', color: '#5b9bd5', count: '3 sub-modules' },
    { icon: 'directions_bus', name: 'Transport', desc: 'Vehicle management, route planning, and transport allocation for students and staff.', color: '#fb7185', count: '3 sub-modules' },
    { icon: 'mail', name: 'Communication', desc: 'Notices, announcements, messages, and notification delivery across the campus.', color: '#4ade80', count: '4 sub-modules' },
    { icon: 'emoji_events', name: 'Activities', desc: 'Clubs, sports, events, and student registration for extracurricular activities.', color: '#e879f9', count: '4 sub-modules' },
    { icon: 'assessment', name: 'Reports', desc: 'Custom report templates and generated analytics dashboards for data-driven decisions.', color: '#facc15', count: '2 sub-modules' },
    { icon: 'shield', name: 'Security', desc: 'Users, roles, permissions, menus, audit logs, workflow management, and security settings.', color: '#dc3545', count: '18 sub-modules' },
  ];

  navScrolled = false;
  isDarkMode = false;

  @HostListener('window:scroll')
  onScroll() {
    this.navScrolled = window.scrollY > 20;
  }

  ngOnInit() {
    document.body.classList.add('landing-body');
    const savedTheme = localStorage.getItem('theme');
    this.isDarkMode = savedTheme === 'dark';
    this.applyTheme();
  }

  ngOnDestroy() {
    document.body.classList.remove('landing-body');
  }

  toggleTheme() {
    this.isDarkMode = !this.isDarkMode;
    localStorage.setItem('theme', this.isDarkMode ? 'dark' : 'light');
    this.applyTheme();
  }

  private applyTheme() {
    if (this.isDarkMode) {
      document.body.classList.add('dark-theme');
    } else {
      document.body.classList.remove('dark-theme');
    }
  }
}
