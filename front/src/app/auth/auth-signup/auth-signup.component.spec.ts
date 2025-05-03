import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { AuthSignupComponent } from './auth-signup.component';
import { AuthService } from '../auth.service';
import { of, throwError } from 'rxjs';
import { By } from '@angular/platform-browser';

describe('AuthSignupComponent', () => {
  let component: AuthSignupComponent;
  let fixture: ComponentFixture<AuthSignupComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {
    const spy = jasmine.createSpyObj('AuthService', ['signup', 'logout']);

    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      declarations: [AuthSignupComponent],
      providers: [{ provide: AuthService, useValue: spy }]
    }).compileComponents();

    fixture = TestBed.createComponent(AuthSignupComponent);
    component = fixture.componentInstance;
    authServiceSpy = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;

    fixture.detectChanges(); // Initialiser la détection de changement
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
    tick(); // simuler l’asynchronicité

    expect(authServiceSpy.signup).toHaveBeenCalledOnceWith({
      username: 'sidi',
      email: 'sidi@test.com',
      password: '123456'
    });
    expect(component.message).toBe('Bienvenue !');
  }));

  it('should show error message on signup error', fakeAsync(() => {
    authServiceSpy.signup.and.returnValue(throwError(() => new Error('Erreur serveur')));

    component.signupForm.setValue({
      username: 'test',
      email: 'test@test.com',
      password: '123456'
    });

    component.onSubmit();
    tick();

    expect(authServiceSpy.signup).toHaveBeenCalled();
    expect(component.message).toBe('Erreur lors de l’inscription');
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
