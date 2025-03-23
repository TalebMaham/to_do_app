import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TaskDetailComponent } from './task-detail.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { TaskService } from './task.service';
import { ProjectService } from '../../project.service';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';

describe('TaskDetailComponent', () => {
  let component: TaskDetailComponent;
  let fixture: ComponentFixture<TaskDetailComponent>;
  let taskServiceSpy: jasmine.SpyObj<TaskService>;
  let projectServiceSpy: jasmine.SpyObj<ProjectService>;

  const fakeTask = {
    id: 1,
    name: 'Tâche Test',
    description: 'Description de test',
    dueDate: '2025-01-01',
    priority: 'HIGH',
    status: 'TODO',
    assignee: { username: 'dev1' }
  };

  const fakeProject = {
    id: 1,
    name: 'Projet Test',
    description: 'Description test',
    startDate: '2024-01-01',
    admin: { id: '10' },
    projectMembers: [
      { user: { id: '1', username: 'User1', email: 'u1@mail.com' }, role: 'MEMBER' }
    ],
    tasks: [fakeTask]
  };

  const fakeHistory = [
    {
      fieldChanged: 'name',
      oldValue: 'Old Name',
      newValue: 'New Name',
      modifiedBy: { username: 'admin' },
      modifiedAt: new Date()
    }
  ];

  beforeEach(async () => {
    const routeStub = {
      paramMap: of({
        get: (key: string) => {
          if (key === 'id') return '1';
          if (key === 'projectId') return '1';
          return null;
        }
      })
    };

    const taskSpy = jasmine.createSpyObj('TaskService', ['updateTask']);
    const projSpy = jasmine.createSpyObj('ProjectService', ['getProjectById']);

    await TestBed.configureTestingModule({
      declarations: [TaskDetailComponent],
      imports: [HttpClientTestingModule, FormsModule, RouterTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: routeStub },
        { provide: TaskService, useValue: taskSpy },
        { provide: ProjectService, useValue: projSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(TaskDetailComponent);
    component = fixture.componentInstance;
    taskServiceSpy = TestBed.inject(TaskService) as jasmine.SpyObj<TaskService>;
    projectServiceSpy = TestBed.inject(ProjectService) as jasmine.SpyObj<ProjectService>;

    // Mock localStorage
    spyOn(localStorage, 'getItem').and.callFake((key: string): string | null => {
      if (key === 'user_id') return '1';
      return null;
    });

    // Appels simulés une seule fois ici
    spyOn(component as any, 'getTaskDetails').and.callFake(() => {
      component.task = fakeTask;
    });

    spyOn(component as any, 'getTaskHistory').and.callFake(() => {
      component.history = fakeHistory;
    });

    projectServiceSpy.getProjectById.and.returnValue(of(fakeProject));
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should detect user as member (not admin)', () => {
    fixture.detectChanges(); // Appelle ngOnInit
    expect(component.isAdmin).toBeFalse(); // admin.id = '10', userId = '1'
    expect(component.isMember).toBeTrue(); // userId = '1' est un membre
  });

  it('should open edit modal and copy task data', () => {
    component.task = fakeTask;
    component.openEditModal();
    expect(component.editTask.name).toEqual('Tâche Test');
  });

  it('should update task via service', () => {
    component.taskId = 1;
    component.projectId = 1;
    component.userId = '1';
    component.editTask = { ...fakeTask };

    taskServiceSpy.updateTask.and.returnValue(of({}));

    // Pas de spyOn répétitif ici
    const getTaskDetailsSpy = (component as any).getTaskDetails as jasmine.Spy;
    const getTaskHistorySpy = (component as any).getTaskHistory as jasmine.Spy;

    component.updateTask();

    expect(taskServiceSpy.updateTask).toHaveBeenCalled();
    expect(getTaskDetailsSpy).toHaveBeenCalled();
    expect(getTaskHistorySpy).toHaveBeenCalled();
  });

  it('should apply priority classes correctly', () => {
    expect(component.getPriorityClass('HIGH')).toBe('text-danger');
    expect(component.getPriorityClass('MEDIUM')).toBe('text-warning');
    expect(component.getPriorityClass('LOW')).toBe('text-success');
    expect(component.getPriorityClass('UNKNOWN')).toBe('');
  });
});
