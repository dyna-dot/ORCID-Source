import { AfterViewInit, Component, NgModule } 
    from '@angular/core';

import { CommonService } 
    from '../../shared/common.service.ts';    
    
@Component({
    selector: 'html-head-ng2',
    template:  scriptTmpl("html-head-ng2-template")
})
export class HtmlHeadComponent {
    
    assetsPath: String;
    
    constructor(private commonSrvc: CommonService) {
        this.commonSrvc.configInfo$
        .subscribe(
            data => {
                this.assetsPath = data.messages['STATIC_PATH'];
            },
            error => {
                console.log('header.component.ts: unable to fetch userInfo', error);
            } 
        );
    }
}