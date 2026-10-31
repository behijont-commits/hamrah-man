package com.hamrahman.app

import org.junit.Assert.*
import org.junit.Test

class TopicModelTest {
    @Test fun permissionsDefaultToLeastPrivilege() {
        val p = TopicPermission("a", "b")
        assertFalse(p.canView); assertFalse(p.canEdit); assertFalse(p.canLogProgress); assertFalse(p.canViewDetails)
    }
    @Test fun newTopicsStartUnlinked() { assertTrue(Topic(name = "نمونه").linkedTopicIds.isEmpty()) }
}
