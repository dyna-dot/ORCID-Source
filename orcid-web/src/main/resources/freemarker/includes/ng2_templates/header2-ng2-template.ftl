<script type="text/ng-template" id="header2-ng2-template">

<div class="header2" >
    <div  *ngIf="!isOauth"> 

        <div class="container" role="banner">
            <div class="logo-search-bar">
                <div class="logo" aria-label="ORCID logo"> 
                    <a href="{{aboutUri}}"><img *ngIf="assetsPath != null" src="{{assetsPath + '/img/orcid-logo.svg'}}" alt="ORCID logo" /></a>
                    <div class="slogan"><@orcid.msg 'public-layout.logo.tagline'/></div>
                    <div class="menu-control"> 
                        <span [hidden]="!openMobileMenu" style="height: 35px" class="close" (click)="toggleMenu()" alt="close menu"> </span>
                        <img [hidden]="openMobileMenu" (click)="toggleMenu()" style="height: 35px" src="{{assetsPath + '/img/glyphicon-menu.svg'}}" alt="open menu"/>
                    </div>
                </div>
                <div class="search" >

                
                <div class="form-group " role="presentation">
                    <div class="search-container" role="search"> 
                        <div class="advance-search-link" role="presentation">
                            <a href="{{getBaseUri()}}/orcid-search/search" class="settings-button" title="<@orcid.msg 'public-layout.search.advanced'/>"><@orcid.msg 'public-layout.search.advanced'/></a>
                        </div>
                        <div class="input-group" role="presentation">
                            <div class="input-group-addon" role="presentation">
                                <div role="presentation" class="search-dropdown" [ngClass]="{'open': searchDropdownOpen}"  (mouseleave)="closeDropdown()">
                                    <div  aria-label="<@orcid.msg 'aria.search-location'/>" role="menu" class="search-dropbtn"   (click)="clickDropdown()"> {{ (headerSearch.searchOption === 'website'? '<@orcid.msg 'layout.public-layout.website'/>':'<@orcid.msg 'layout.public-layout.registry'/>') | titlecase }} <span [ngClass]="{'dropdown-arrow': !searchDropdownOpen, 'dropdown-arrow-up': searchDropdownOpen}"></span> </div>
                                    <ul class="dropdown-content" role="presentation">
                                        <div (click)="clickDropdown('registry')"> {{'<@orcid.msg 'layout.public-layout.registry'/>'| titlecase }} </div>
                                        <div (click)="clickDropdown('website')"> {{'<@orcid.msg 'layout.public-layout.website'/>'| titlecase  }} </div>
                                    </ul>
                                </div>  
                            </div>
                            <input (keyup.enter)="searchSubmit()" [(ngModel)]="headerSearch.searchInput" class="form-control" name="search" type="text" placeholder="<@orcid.msg 'public-layout.search'/>"/>
                            <div aria-label="<@orcid.msg 'orcid_bio_search.btnsearch'/>" class="input-group-addon" role="presentation">
                                <span role="button" aria-label="<@orcid.msg 'orcid_bio_search.btnsearch'/>" class="glyphicon glyphicon-search" (click)="searchSubmit()"></span> 
                            </div>
                        </div>
                    </div>
                    <language-ng2></language-ng2>
                </div>
                         <#--  
                        <div class="conditions" >
                            <p>                         
                                <@orcid.msg 'public-layout.search.terms1'/><a
                                    href="{{aboutUri}}/legal"><@orcid.msg
                                    'public-layout.search.terms2'/></a><@orcid.msg
                                'public-layout.search.terms3'/>
                            </p>
                        </div>  
                        -->
                </div>
            </div>
        </div>

        <div class="menu-bar"  [hidden]="!openMobileMenu && isMobile"  (mouseleave)="mouseLeave()" role="menu">
                <!--  Desktop / Tablet menu -->             
                <div class="container container-menu"  role="presentation"> 
                <ul class="menu " resize  role="presentation">
                    <!-- FOR RESEARCHERS -->
                    <li class="first expanded" role="presentation" [ngClass]="{'open': mobileMenu.RESEARCHERS}" (mouseenter)="menuHandler('RESEARCHERS', $event)" (click)="menuHandler('RESEARCHERS', $event)" >
                        <a href="{{aboutUri}}/about/what-is-orcid/mission" title="" role="menu-item"><@orcid.msg 'public-layout.for_researchers'/></a>
                        <ul class="menu lang-fixes" *ngIf="!userInfo['REAL_USER_ORCID']" aria-label="submenu">
                            <!-- Mobile view Only -->
                            <li class="leaf    " [hidden]="!isMobile"><a href="{{getBaseUri()}}" title="" role="menu-item"><@orcid.msg 'public-layout.for_researchers'/></a></li>
                    
                            <!-- Menu -->
                            <li class="leaf last"><a ${(nav=="signin")?then('class="active" ', '')} href="{{getBaseUri()}}/signin" role="menu-item"><@orcid.msg 'public-layout.sign_in'/></a></li>                                   
                            <li class="leaf last"><a ${(nav=="register")?then('class="active" ', '')} href="{{getBaseUri()}}/register" role="menu-item"><@orcid.msg 'public-layout.register'/></a></li>                                                                                                                          
                            <li class="leaf last"><a href="{{getBaseUri()}}/content/initiative" role="menu-item"><@orcid.msg 'manage_delegators.learn_more.link.text' /></a></li>
                        </ul>
                        <ul class="menu lang-fixes" *ngIf="userInfo['REAL_USER_ORCID']"  aria-label="submenu">
                            <li role="presentation" >
                                <a ${(nav=="record")?then('class="active" ', '')}href="{{getBaseUri()}}/my-orcid" role="menu-item" aria-labelby="my-orcid-menu-item">
                                    <div *ngIf="userInfo['IN_DELEGATION_MODE'] == 'true'" role="menu-item" id="my-orcid-menu-item">
                                        <@orcid.msg 'public-layout.my_orcid'/>
                                    </div >
                                    <div *ngIf="userInfo['IN_DELEGATION_MODE'] == 'false'" id="my-orcid-menu-item">
                                        <@orcid.msg 'public-layout.my_orcid_record'/>
                                    </div >
                                </a>
                            </li>
                            <li role="presentation">
                                {{retrieveUnreadCount()}}
                                <a role="menu-item" ${(nav=="notifications")?then('class="active" ', '')} href="{{getBaseUri()}}/inbox">${springMacroRequestContext.getMessage("workspace.notifications")} <span *ngIf="getUnreadCount > 0">({{getUnreadCount}})</span></a>
                            </li>
                            <li role="presentation">
                                <a ${(nav=="settings")?then('class="active" ', '')} href="{{getBaseUri()}}/account" id="accountSettingMenuLink"><@orcid.msg 'public-layout.account_setting'/></a>
                            </li>
                            
                            <!-- Developer tools -->
                            <li role="presentation" *ngIf="(userInfo['IN_DELEGATION_MODE'] == 'false' || userInfo['DELEGATED_BY_ADMIN'] == 'true') && userInfo['MEMBER_MENU']=='true'"><a role="menu-item" ${(nav=="developer-tools")?then('class="active" ', '')}href="{{getBaseUri()}}/group/developer-tools">${springMacroRequestContext.getMessage("workspace.developer_tools")}</a></li>
                            <li *ngIf="(userInfo['IN_DELEGATION_MODE'] == 'false' || userInfo['DELEGATED_BY_ADMIN'] == 'true') && userInfo['MEMBER_MENU']!='true'"><a role="menu-item" ${(nav=="developer-tools")?then('class="active" ', '')}href="{{getBaseUri()}}/developer-tools">${springMacroRequestContext.getMessage("workspace.developer_tools")}</a></li>
                            
                            <!-- Admin menu -->
                            <li  role="presentation" role="presentation" *ngIf="userInfo['ADMIN_MENU']"><a role="menu-item" ${(nav=="members")?then('class="active" ', '')}href="{{getBaseUri()}}/manage-members"><@orcid.msg 'admin.members.workspace_link' /></a></li>
                            <li  role="presentation" *ngIf="userInfo['ADMIN_MENU']"><a  role="menu-item" ${(nav=="admin")?then('class="active" ', '')}href="{{getBaseUri()}}/admin-actions"><@orcid.msg 'admin.workspace_link' /></a></li>
                                
                            <!-- Self service menu -->
                            <li  role="presentation" *ngIf="userInfo['SELF_SERVICE_MENU']"><a role="menu-item" ${(nav=="self-service")?then('class="active" ', '')}href="{{getBaseUri()}}/self-service"><@orcid.msg 'workspace.self_service' /></a></li>
                                    
                            <li  role="presentation" class="leaf last"><a role="menu-item" href="{{getBaseUri()}}/content/initiative"><@orcid.msg 'manage_delegators.learn_more.link.text' /></a></li>
                        </ul>
                    </li>

                    <!-- DRUPAL WEBSITE MENUS -->
                    <!-- FOR ORGANIZATIONS -->
                    <li role="presentation" class="expanded" [ngClass]="{'open': mobileMenu.ORGANIZATIONS}" (mouseenter)="menuHandler('ORGANIZATIONS', $event)" (click)="menuHandler('ORGANIZATIONS', $event)">
                        <a href="{{aboutUri}}/organizations" role="menu-item"><@orcid.msg 'public-layout.for_organizations'/></a>
                        <ul class="menu lang-fixes"  aria-label="submenu">
                            <!-- Mobile view Only -->
                            <li role="presentation" class="first leaf" [hidden]="!isMobile" >
                                <a role="menu-item" href="{{aboutUri}}/organizations" ><@orcid.msg 'public-layout.for_organizations'/></a>
                            </li>

                            <li role="presentation" class="first leaf">
                                <a role="menu-item" href="{{aboutUri}}/organizations/funders" class="russian-fix" ><@orcid.msg 'public-layout.funders'/></a> <!-- Updated according Drupal website structure -->
                            </li>
                            <li role="presentation" class="leaf">
                                <a role="menu-item" href="{{aboutUri}}/organizations/institutions" ><@orcid.msg 'public-layout.research_organizations'/></a> <!-- Updated according Drupal website structure -->
                            </li>
                            <li role="presentation" class="leaf">
                                <a role="menu-item" href="{{aboutUri}}/organizations/publishers" > <@orcid.msg 'public-layout.publishers'/></a> <!-- Updated according Drupal website structure -->
                            </li>
                            <li role="presentation" class="leaf">
                                <a role="menu-item" href="{{aboutUri}}/organizations/associations" ><@orcid.msg 'public-layout.associations'/></a> <!-- Updated according Drupal website structure -->
                            </li>
                            <li role="presentation" class="last leaf">
                                <a role="menu-item" href="{{aboutUri}}/organizations/integrators" ><@orcid.msg 'public-layout.integrators'/></a> <!-- Updated according Drupal website structure -->
                            </li>
                        </ul>
                    </li>
                    <!-- ABOUT -->
                    <li role="presentation" class="expanded" [ngClass]="{'open': mobileMenu.ABOUT}" (mouseenter)="menuHandler('ABOUT', $event)"  (click)="menuHandler('ABOUT', $event)" >
                        <a href="{{aboutUri}}/about" role="menu-item"><@orcid.msg 'public-layout.about'/></a>

                        <ul class="menu lang-fixes"  aria-label="submenu">
                            <!-- Mobile view Only -->
                            <li  role="presentation" [hidden]="!isMobile" ><a role="menu-item" href="{{aboutUri}}/about"
                                class="first leaf    "><@orcid.msg
                                    'public-layout.about'/></a></li>
                            <!-- What is ORCID? -->
                            <li role="presentation" class="first expanded">
                                <a role="menu-item" href="{{aboutUri}}/about/what-is-orcid"><@orcid.msg 'public-layout.what_is_orcid'/></a>
                       
                            </li>
                            <!-- The ORCID Team -->
                            <li role="presentation" class="leaf"><a role="menu-item" href="{{aboutUri}}/about/team" title=""><@orcid.msg
                                    'public-layout.the_orcid_team'/></a></li>
                            <!-- The ORCID Comunity -->
                            <li role="presentation" class="expanded">
                                <a role="menu-item" href="{{aboutUri}}/about/community"><@orcid.msg 'public-layout.the_orcid_community'/></a>
                             
                            </li>
                            <!-- Membership -->
                            <li role="presentation" class="expanded">
                                <a role="menu-item" href="{{aboutUri}}/about/membership"><@orcid.msg 'public-layout.membership'/></a>
        
                            </li>
                            <!-- News -->
                            <li role="presentation" class="leaf">
                                <a role="menu-item" href="{{aboutUri}}/about/news/news"><@orcid.msg 'public-layout.news'/></a>
                            </li>
                            <!-- Events -->
                            <li role="presentation" class="last expanded">
                                <a role="menu-item" href="{{aboutUri}}/about/events" title=""><@orcid.msg 'public-layout.events'/></a>
                            </li>
                        </ul>
                    </li>
                    <!-- HELP -->
                    <li role="presentation" class="expanded" [ngClass]="{'open': mobileMenu.HELP}" (mouseenter)="menuHandler('HELP', $event)"  (click)="menuHandler('HELP', $event)">
                        <a role="menu-item" href="{{aboutUri}}/help"><@orcid.msg 'public-layout.help' /></a>
                        <ul  [hidden]="!isMobile" class="menu lang-fixes"  aria-label="submenu">
                            <!-- Mobile view Only -->
                            <li role="presentation" class="first leaf    "  [hidden]="!isMobile">
                                <a role="menu-item" href="{{aboutUri}}/help"><@orcid.msg 'public-layout.help'/></a>
                            </li>
                            <li role="presentation" class="first leaf">
                                <a role="menu-item" href="{{aboutUri}}/faq-page" title=""><@orcid.msg 'public-layout.faq'/></a>
                            </li>
                            <li role="presentation" class="leaf">
                                <a role="menu-item" href="{{aboutUri}}/help/contact-us" title=""><@orcid.msg 'public-layout.contact_us'/></a>
                            </li>
                            <li role="presentation" class="leaf">
                                <a role="menu-item" href="https://support.orcid.org/hc/en-us/community/topics" title=""><@orcid.msg 'public-layout.give_feedback'/></a>
                            </li>
                            <li role="presentation" class="last leaf">
                                <a role="menu-item" href="<@orcid.msg 'common.kb_uri_help_center_home'/>" title=""><@orcid.msg 'public-layout.knowledge_base'/></a>
                            </li>
                        </ul>
                    </li>
                    
                    <!-- SIGN IN/OUT -->
                    <li role="presentation" class="last leaf" [ngClass]="{'open': mobileMenu.SIGNIN}" (mouseenter)="menuHandler('SIGNIN', $event)" (click)="menuHandler('SIGNIN', $event)">                    
                        <a *ngIf="!userInfo['REAL_USER_ORCID']" href="{{getBaseUri()}}/signin" role="menu-item"><@orcid.msg 'public-layout.sign_in'/></a>                    
                        <a *ngIf="userInfo['REAL_USER_ORCID']" href="{{getBaseUri()}}/signout" role="menu-item"><@orcid.msg 'public-layout.sign_out'/></a>
                    </li>                    

                </ul>  
                </div>
        </div>

        <p class="header2-see-more container" role="Complementary" aria-label="<@orcid.msg 'aria.orcid-statistics'/>">{{liveIds}} <@orcid.msg
             'public-layout.amount_ids'/> <a href="{{getBaseUri()}}/statistics"
             title=""><@orcid.msg 'public-layout.see_more'/></a>
        </p>

    </div>   
</div> 
</script>