import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OfficialForms } from './official-forms';

describe('OfficialForms', () => {
  let component: OfficialForms;
  let fixture: ComponentFixture<OfficialForms>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OfficialForms],
    }).compileComponents();

    fixture = TestBed.createComponent(OfficialForms);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
