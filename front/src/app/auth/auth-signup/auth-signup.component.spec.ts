import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { AuthSignupComponent } from './auth-signup.component';
import { AuthService } from '../auth.service';
import { of, throwError } from 'rxjs';
import { By } from '@angular/platform-browser';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { Component } from '@angular/core';

@Component({
  standalone: true,
  template: '<p>Signin</p>',
})
class DummySigninComponent {}

describe('AuthSignupComponent', () => {
  let component: AuthSignupComponent;
  let fixture: ComponentFixture<AuthSignupComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    const authSpy = jasmine.createSpyObj('AuthService', ['signup', 'logout']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule,
        RouterTestingModule.withRoutes([
          { path: 'auth/signin', component: DummySigninComponent }
        ]),
        DummySigninComponent // important ici pour standalone
      ],
      declarations: [AuthSignupComponent],
      providers: [
        { provide: AuthService, useValue: authSpy },
        { provide: Router, useValue: routerSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AuthSignupComponent);
    component = fixture.componentInstance;
    authServiceSpy = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should have an invalid form when empty', () => {
    expect(component.signupForm.valid).toBeFalse();
  });

  it('should call AuthService.signup on valid form submit and show success message', fakeAsync(() => {
    const mockResponse = { message: 'Bienvenue !' };
    authServiceSpy.signup.and.returnValue(of(mockResponse));

    component.signupForm.setValue({
      username: 'sidi',
      email: 'sidi@test.com',
      password: '123456'
    });

    component.onSubmit();

    // Abonnement immédiat
    tick(0);
    expect(authServiceSpy.signup).toHaveBeenCalledOnceWith({
      username: 'sidi',
      email: 'sidi@test.com',
      password: '123456'
    });

    // ✅ Message visible avant effacement
    expect(component.message).toBe('Bienvenue !');

    // ⏳ 3 secondes pour effacer le message
    tick(3000);
    expect(component.message).toBe('');

    // ⏳ 3 secondes pour le logout + redirection
    tick(3000);
    expect(authServiceSpy.logout).toHaveBeenCalled();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/auth/signin']);
  }));

  it('should show error message on signup error', fakeAsync(() => {
    authServiceSpy.signup.and.returnValue(throwError(() => new Error('Erreur serveur')));

    component.signupForm.setValue({
      username: 'test',
      email: 'test@test.com',
      password: '123456'
    });

    component.onSubmit();
    tick(); // pour le subscribe

    expect(authServiceSpy.signup).toHaveBeenCalled();
    expect(component.message).toBe('Erreur lors de l’inscription');

    // après 3 secondes le message est effacé
    tick(3000);
    expect(component.message).toBe('');
  }));

  it('should disable the submit button when the form is invalid', () => {
    const button = fixture.debugElement.query(By.css('button')).nativeElement;
    expect(button.disabled).toBeTrue();
  });

  it('should enable the submit button when the form is valid', () => {
    component.signupForm.setValue({
      username: 'test',
      email: 'test@test.com',
      password: '123456'
    });
    fixture.detectChanges();

    const button = fixture.debugElement.query(By.css('button')).nativeElement;
    expect(button.disabled).toBeFalse();
  });
});
