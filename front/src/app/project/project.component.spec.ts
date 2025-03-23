import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ProjectComponent } from './project.component';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ProjectService } from './project.service';
import { of, throwError } from 'rxjs';
import { Router } from '@angular/router';

const mockProjects = [
  { id: 1, name: 'Projet A', description: 'Description A', startDate: '2024-01-01' },
  { id: 2, name: 'Projet B', description: 'Description B', startDate: '2024-02-01' },
];

describe('ProjectComponent', () => {
  let component: ProjectComponent;
  let fixture: ComponentFixture<ProjectComponent>;
  let projectServiceSpy: jasmine.SpyObj<ProjectService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    localStorage.setItem('user_id', '1');

    const projectSpy = jasmine.createSpyObj('ProjectService', [
      'createProject',
      'getProjectsByUser',
      'getProjectsByAdmin',
      'getProjects',
    ]);
    const routerMock = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      declarations: [ProjectComponent],
      imports: [ReactiveFormsModule, HttpClientTestingModule],
      providers: [
        { provide: ProjectService, useValue: projectSpy },
        { provide: Router, useValue: routerMock },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ProjectComponent);
    component = fixture.componentInstance;
    projectServiceSpy = TestBed.inject(ProjectService) as jasmine.SpyObj<ProjectService>;
    routerSpy = TestBed.inject(Router) as jasmine.SpyObj<Router>;
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should load projects on init', () => {
    projectServiceSpy.getProjectsByUser.and.returnValue(of(mockProjects));
    component.ngOnInit();
    expect(projectServiceSpy.getProjectsByUser).toHaveBeenCalledWith(1);
    expect(component.projects.length).toBe(2);
  });

  it('should call createProject and reload projects on valid submit', fakeAsync(() => {
    projectServiceSpy.createProject.and.returnValue(of({ success: true }));
    projectServiceSpy.getProjectsByUser.and.returnValue(of(mockProjects));

    component.projectForm.setValue({
      name: 'New Project',
      description: 'Valid description project',
      startDate: '2025-04-01',
      adminId: 1,
    });

    component.onSubmit();
    tick();

    expect(projectServiceSpy.createProject).toHaveBeenCalled();
    expect(component.message).toBe('Projet créé avec succès !');
    expect(projectServiceSpy.getProjectsByUser).toHaveBeenCalled();
  }));

  it('should show error message if project creation fails', fakeAsync(() => {
    projectServiceSpy.createProject.and.returnValue(throwError(() => new Error('Erreur serveur')));

    component.projectForm.setValue({
      name: 'Error Project',
      description: 'Invalid or duplicate project',
      startDate: '2025-04-01',
      adminId: 1,
    });

    component.onSubmit();
    tick();

    expect(component.message).toBe('Erreur lors de la création du projet.');
  }));

  it('should navigate to detail on viewProjectDetail()', () => {
    component.viewProjectDetail(5);
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/project-detail', 5]);
  });
});
