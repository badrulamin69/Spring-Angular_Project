import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  loading = false;
  errorMessage = '';
  sessionExpired = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.loginForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(2), this.noWhitespaceValidator]],
      password: ['', [Validators.required, Validators.minLength(3), this.noWhitespaceValidator]],
      rememberMe: [false]
    });
  }

  ngOnInit() {
    this.sessionExpired = this.route.snapshot.queryParamMap.get('sessionExpired') === 'true';
    if (this.sessionExpired) {
      this.errorMessage = 'Your session has expired. Please log in again.';
    }
  }

  noWhitespaceValidator(control: AbstractControl): ValidationErrors | null {
    const value = control.value;
    if (value && typeof value === 'string' && value.trim().length === 0) {
      return { whitespace: true };
    }
    return null;
  }

  fillDemo(username: string, password: string): void {
    this.loginForm.patchValue({ username, password });
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    const formValue = this.loginForm.value;
    const credentials = {
      username: formValue.username.trim(),
      password: formValue.password,
      rememberMe: formValue.rememberMe
    };

    this.authService.login(credentials).subscribe({
      next: () => {
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.loading = false;
        if (err.status === 0) {
          this.errorMessage = 'Unable to connect to server. Please check if the backend is running.';
        } else if (err.status === 401) {
          this.errorMessage = err.error?.message || 'Invalid username or password';
        } else if (err.status === 403) {
          this.errorMessage = err.error?.message || 'Access denied. Please contact administrator.';
        } else if (err.status === 409) {
          this.errorMessage = 'A conflict occurred. Please try again.';
        } else {
          this.errorMessage = err.error?.message || 'An error occurred. Please try again.';
        }
      }
    });
  }
}
