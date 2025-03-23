import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ProjectDetailComponent } from './project-detail.component';
import { ActivatedRoute } from '@angular/router';
import { of, throwError } from 'rxjs';
import { RouterTestingModule } from '@angular/router/testing';
import { ProjectService } from '../project.service';
import { TaskService } from './task-detail/task.service';
import { FormsModule } from '@angular/forms';

describe('ProjectDetailComponent', () => {
  let component: ProjectDetailComponent;
  let fixture: ComponentFixture<ProjectDetailComponent>;
  let projectServiceSpy: jasmine.SpyObj<ProjectService>;
  let taskServiceSpy: jasmine.SpyObj<TaskService>;

  const fakeProject = {
    id: 1,
    name: 'Projet Test',
    description: 'Description test',
    startDate: '2024-01-01',
    admin: { id: '10' },
    projectMembers: [
      { user: { id: 2, username: 'Alice', email: 'alice@mail.com' }, role: 'MEMBER' },
    ],
    tasks: [
      { id: 1, name: 'Tâche 1', description: 'Desc', dueDate: '2024-03-01', priority: 'HIGH', status: 'TODO', assignee: null }
    ]
  };

  beforeEach(async () => {
    const projectServiceMock = jasmine.createSpyObj('ProjectService', ['getProjectById', 'addMemberToProject', 'createTask']);
    const taskServiceMock = jasmine.createSpyObj('TaskService', ['assignTask']);

    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, FormsModule],
      declarations: [ProjectDetailComponent],
      providers: [
        { provide: ProjectService, useValue: projectServiceMock },
        { provide: TaskService, useValue: taskServiceMock },
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: new Map([['id', '1']]) } }
        }
      ]
    }).compileComponents();

    projectServiceSpy = TestBed.inject(ProjectService) as jasmine.SpyObj<ProjectService>;
    taskServiceSpy = TestBed.inject(TaskService) as jasmine.SpyObj<TaskService>;

    fixture = TestBed.createComponent(ProjectDetailComponent);
    component = fixture.componentInstance;

    // Simuler l’utilisateur connecté
    spyOn(localStorage, 'getItem').and.callFake((key: string) => {
      if (key === 'user_id') return '10';
      return null;
    });
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load project details on init', () => {
    projectServiceSpy.getProjectById.and.returnValue(of(fakeProject));
    fixture.detectChanges();

    expect(component.project).toEqual(fakeProject);
    expect(component.isAdmin).toBeTrue();
  });

  it('should handle error when loading project fails', () => {
    projectServiceSpy.getProjectById.and.returnValue(throwError(() => new Error('Erreur serveur')));
    fixture.detectChanges();

    expect(component.message).toContain('Erreur');
  });

  it('should add member successfully', () => {
    component.project = fakeProject;
    component.newMemberEmail = 'new@user.com';
    component.newMemberRole = 'MEMBER';

    projectServiceSpy.addMemberToProject.and.returnValue(of({}));
    projectServiceSpy.getProjectById.and.returnValue(of(fakeProject)); // pour recharger

    component.addMember();

    expect(projectServiceSpy.addMemberToProject).toHaveBeenCalledWith(1, 'new@user.com', 'MEMBER');
    expect(component.message).toContain('succès');
  });

  it('should handle task creation', () => {
    component.project = fakeProject;
    component.newTask = {
      name: 'Nouvelle tâche',
      description: 'Une description',
      dueDate: '2025-04-01',
      priority: 'LOW'
    };

    projectServiceSpy.createTask.and.returnValue(of({}));
    projectServiceSpy.getProjectById.and.returnValue(of(fakeProject));

    component.createTask();

    expect(projectServiceSpy.createTask).toHaveBeenCalledWith(1, jasmine.objectContaining({
      name: 'Nouvelle tâche',
      description: 'Une description',
      dueDate: '2025-04-01',
      priority: 'LOW',
      userId: '10'
    }));
  });

  it('should assign task', () => {
    component.project = fakeProject;
    component.selectedTaskId = 1;
    component.selectedUserId = 2;

    taskServiceSpy.assignTask.and.returnValue(of({}));
    projectServiceSpy.getProjectById.and.returnValue(of(fakeProject));

    component.assignTask();

    expect(taskServiceSpy.assignTask).toHaveBeenCalledWith(1, 2, 1);
  });
});
