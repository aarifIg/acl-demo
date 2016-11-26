package com.testacl

import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission
import org.springframework.transaction.annotation.Transactional

class ReportService {

    static transactional = false

    def aclPermissionFactory
    def aclService
    def aclUtilService
    def springSecurityService

    void addPermission(Report report, String username, int permission) {
        addPermission report, username,
                aclPermissionFactory.buildFromMask(permission)
    }

    @PreAuthorize("hasPermission(#report, admin)")
    @Transactional
    void addPermission(Report report, String username,
                       Permission permission) {
        aclUtilService.addPermission report, username, permission
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_USER')")
    Report create(String name) {
        Report report = new Report(name: name)
        report.save()

        // Grant the current principal administrative permission
        addPermission report, springSecurityService.authentication.name,
                BasePermission.ADMINISTRATION

        report
    }

    @PreAuthorize("hasPermission(#id, 'com.testacl.Report', read)")
    Report get(long id) {
        Report.get id
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostFilter("hasPermission(filterObject, read)")
    List<Report> list(Map params) {
        Report.list params
    }

    int count() {
        Report.count()
    }

    @Transactional
    @PreAuthorize("hasPermission(#report, write)")
    void update(Report report, String name) {
        report.name = name
    }

    @Transactional
    @PreAuthorize("hasPermission(#report, delete)")
    void delete(Report report) {
        report.delete()

        // Delete the ACL information as well
        aclUtilService.deleteAcl report
    }

    @Transactional
    @PreAuthorize("hasPermission(#report, admin)")
    void deletePermission(Report report, String username, Permission permission) {
        def acl = aclUtilService.readAcl(report)

        // Remove all permissions associated with this particular
        // recipient (string equality to KISS)
        acl.entries.eachWithIndex { entry, i ->
            if (entry.sid.equals(recipient) &&
                    entry.permission.equals(permission)) {
                acl.deleteAce i
            }
        }

        aclService.updateAcl acl
    }
}
