import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';
import { AuthSignupComponent } from './auth-signup.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { AuthService } from '../auth.service';

jest.mock('../auth.service'); // ✅ Mock automatique de AuthService

describe('AuthSignupComponent', () => {
  let component: AuthSignupComponent;
  let fixture: ComponentFixture<AuthSignupComponent>;
  let mockAuthService: jest.Mocked<AuthService>;

  beforeEach(async () => {
    // ✅ Création du mock automatique avec Jest
    mockAuthService = new AuthService(null as any) as jest.Mocked<AuthService>;
    mockAuthService.signup = jest.fn();

    await TestBed.configureTestingModule({
      imports: [
        AuthSignupComponent, // ✅ Composant standalone
        ReactiveFormsModule,
        HttpClientTestingModule, // ✅ Simulation des requêtes HTTP
      ],
      providers: [{ provide: AuthService, useValue: mockAuthService }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AuthSignupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    jest.clearAllMocks(); // ✅ Nettoyage des mocks après chaque test
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show success message on valid submission', () => {
    const mockResponse = { message: 'Utilisateur inscrit avec succès' };
    mockAuthService.signup.mockReturnValue(of(mockResponse)); // ✅ Correction du mock

    component.signupForm.setValue({
      username: 'testuser',
      email: 'test@mail.com',
      password: 'password123',
    });

    component.onSubmit();
    expect(component.message).toBe('Utilisateur inscrit avec succès');
  });

  it('should show error message on failure', () => {
    mockAuthService.signup.mockReturnValue(throwError(() => new Error('Erreur'))); // ✅ Correction du mock

    component.signupForm.setValue({
      username: 'testuser',
      email: 'test@mail.com',
      password: 'password123',
    });

    component.onSubmit();
    expect(component.message).toBe("Erreur lors de l'inscription");
  });
});
