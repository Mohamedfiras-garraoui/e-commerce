import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ThemeCustomizationComponent } from './theme-customization.component';

describe('ThemeCustomizationComponent', () => {
  let component: ThemeCustomizationComponent;
  let fixture: ComponentFixture<ThemeCustomizationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ThemeCustomizationComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ThemeCustomizationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
