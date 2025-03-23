import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AuthSigninComponent } from './auth-signin.component';
import { ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../auth.service';
import { of, throwError } from 'rxjs';
import { Router } from '@angular/router';

describe('AuthSigninComponent', () => {
  let component: AuthSigninComponent;
  let fixture: ComponentFixture<AuthSigninComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    authServiceSpy = jasmine.createSpyObj('AuthService', ['signin', 'isLoggedIn']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      declarations: [AuthSigninComponent],
      imports: [ReactiveFormsModule],
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AuthSigninComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should disable submit button when form is invalid', () => {
    component.signinForm.setValue({ username: '', password: '' });
    expect(component.signinForm.invalid).toBeTrue();
  });

  it('should enable submit button when form is valid', () => {
    component.signinForm.setValue({ username: 'test', password: 'pass' });
    expect(component.signinForm.valid).toBeTrue();
  });

  it('should show error message on signin error', () => {
    const errorResponse = { error: { error: 'Invalid credentials' } };

    component.signinForm.setValue({ username: 'bad', password: 'bad' });
    authServiceSpy.signin.and.returnValue(throwError(() => errorResponse));

    component.onSubmit();

    expect(component.message).toBe('Invalid credentials');
  });

  it('should call AuthService.signin and navigate on valid form', () => {
    const response = {
      token: 'abc123',
      user_name: 'sidi',
      message: 'Connexion réussie !'
    };

    authServiceSpy.signin.and.returnValue(of(response));
    authServiceSpy.isLoggedIn.and.returnValue(true);

    component.signinForm.setValue({ username: 'sidi', password: 'secret' });
    component.onSubmit();

    expect(authServiceSpy.signin).toHaveBeenCalledWith({ username: 'sidi', password: 'secret' });
    expect(localStorage.getItem('token')).toBe('abc123');
    expect(localStorage.getItem('user_name')).toBe('sidi');
    expect(component.message).toBe('Connexion réussie !');
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/project']);
  });
});
